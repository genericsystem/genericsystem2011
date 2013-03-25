package org.genericsystem.core;

import java.io.Serializable;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.Constraint.CheckingType;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractAwareIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.genericsystem.snapshot.PseudoConcurrentSnapshot;
import org.genericsystem.system.CascadeRemoveSystemProperty;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class CacheImpl extends AbstractContext implements Cache {
	private static final long serialVersionUID = 6124326077696104707L;

	private AbstractContext subContext;

	private InternalCache internalCache;

	private transient Map<Generic, TimestampedDependencies> compositeDependenciesMap;

	private transient Map<Generic, TimestampedDependencies> inheritingDependenciesMap;

	public CacheImpl(Context subContext) {
		this.subContext = (AbstractContext) subContext;
		clear();
	}

	@Override
	public void clear() {
		compositeDependenciesMap = new HashMap<Generic, TimestampedDependencies>();
		inheritingDependenciesMap = new HashMap<Generic, TimestampedDependencies>();
		internalCache = new InternalCache();
	}

	<T extends Generic> T insert(Generic generic) throws RollbackException {
		try {
			return this.<T> internalInsert(generic);
		} catch (AbstractConstraintViolationException e) {
			rollback(e);
		}
		throw new IllegalStateException();// Unreachable;
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> T internalInsert(Generic generic) throws AbstractConstraintViolationException {
		getInternalContext().addGeneric(generic);
		return (T) generic;
	}

	<T extends Generic> T bindPrimaryByValue(Generic primaryAncestor, Serializable value, int metaLevel) {
		T implicit = findPrimaryByValue(primaryAncestor, value, metaLevel);
		return implicit != null ? implicit : this.<T> insert(new GenericImpl().initializePrimary(value, metaLevel, new Generic[] { primaryAncestor }, Statics.EMPTY_GENERIC_ARRAY));
	}

	@Override
	TimestampedDependencies getDirectInheritingsDependencies(Generic directSuper) {
		TimestampedDependencies dependencies = inheritingDependenciesMap.get(directSuper);
		if (dependencies == null) {
			TimestampedDependencies result = inheritingDependenciesMap.put(directSuper, dependencies = new CacheDependencies(subContext.getDirectInheritingsDependencies(directSuper)));
			assert result == null;
		}
		return dependencies;
	}

	@Override
	TimestampedDependencies getCompositeDependencies(Generic component) {
		TimestampedDependencies dependencies = compositeDependenciesMap.get(component);
		if (dependencies == null) {
			TimestampedDependencies result = compositeDependenciesMap.put(component, dependencies = new CacheDependencies(subContext.getCompositeDependencies(component)));
			assert result == null;
		}
		return dependencies;
	}

	public void pickNewTs() throws RollbackException {
		if (subContext instanceof Cache)
			((CacheImpl) subContext).pickNewTs();
		else {
			long ts = getTs();
			subContext = new Transaction(getEngine());
			assert getTs() > ts;
		}
	}

	private void checkIsAlive(Generic generic) throws AbstractConstraintViolationException {
		if (!isAlive(generic))
			throw new AliveConstraintViolationException(generic + " is not alive");
	}

	void remove(Generic generic) throws RollbackException {
		try {
			checkIsAlive(generic);
			List<Generic> componentsForCascadeRemove = getComponentsForCascadeRemove(generic);
			internalRemove(generic);
			for (Generic component : componentsForCascadeRemove)
				internalRemove(component);
		} catch (AbstractConstraintViolationException e) {
			rollback(e);
		}
	}

	private List<Generic> getComponentsForCascadeRemove(Generic generic) throws AbstractConstraintViolationException {
		Generic[] components = ((GenericImpl) generic).components;
		List<Generic> componentsForCascadeRemove = new ArrayList<>();
		for (int axe = 0; axe < components.length; axe++)
			if (((GenericImpl) generic).isSystemPropertyEnabled(this, CascadeRemoveSystemProperty.class, axe))
				componentsForCascadeRemove.add(components[axe]);
		return componentsForCascadeRemove;
	}

	private void internalRemove(Generic node) throws AbstractConstraintViolationException {
		// assert !node.getValue().equals("Power");
		checkIsAlive(node);
		removeDependencies(node);
		if (isAlive(node))
			internalCache.removeGeneric(node);
	}

	private void removeDependencies(final Generic node) throws AbstractConstraintViolationException {
		Iterator<Generic> inheritingsDependeciesIterator = getDirectInheritingsDependencies(node).iterator(getTs());
		while (inheritingsDependeciesIterator.hasNext()) {
			Generic inheritingDependency = inheritingsDependeciesIterator.next();
			if (isAlive(inheritingDependency))
				throw new ReferentialIntegrityConstraintViolationException(inheritingDependency + " is an inheritance dependency for ancestor " + node);
		}
		Iterator<Generic> compositeDependenciesIterator = getCompositeDependencies(node).iterator(getTs());
		while (compositeDependenciesIterator.hasNext()) {
			Generic compositeDependency = compositeDependenciesIterator.next();
			if (!node.equals(compositeDependency)) {
				Generic[] compositionComponents = ((GenericImpl) compositeDependency).components;
				for (int componentPos = 0; componentPos < compositionComponents.length; componentPos++)
					if (compositionComponents[componentPos].equals(node) && compositeDependency.isReferentialIntegrity(this, componentPos))
						throw new ReferentialIntegrityConstraintViolationException(compositeDependency + " is Referential Integrity for ancestor " + node + " by component position : " + componentPos);
				internalRemove(compositeDependency);
			}
		}
	}

	@Override
	public void flush() throws RollbackException {
		Exception cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				internalCache.checkConstraints();
				internalCache.flush();
				clear();
				return;
			} catch (ConcurrencyControlException e) {
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
				} catch (InterruptedException ex) {
					throw new IllegalStateException(ex);
				}
				if (attempt > Statics.ATTEMPTS / 2)
					log.info("MvccException : " + e + " attempt : " + attempt);
				cause = e;
				pickNewTs();
				continue;
			} catch (Exception e) {
				rollback(e);
			}
		rollback(cause);
	}

	protected void rollback(Exception e) throws RollbackException {
		clear();
		throw new RollbackException(e);
	}

	@Override
	public boolean isAlive(Generic generic) {
		return internalCache.isAlive(generic);
	}

	@Override
	public long getTs() {
		return subContext.getTs();
	}

	@Override
	public <T extends Engine> T getEngine() {
		return subContext.getEngine();
	}

	public AbstractContext getSubContext() {
		return subContext;
	}

	@Override
	InternalCache getInternalContext() {
		return internalCache;
	}

	<T extends Generic> T update(Generic old, Serializable value) {
		return reInsert(orderAndRemoveDependencies(old).iterator(), ((GenericImpl) old).getImplicit(), bindPrimaryByValue(old.<GenericImpl> getImplicit().supers[0], value, old.getMetaLevel()));
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T reInsert(Iterator<Generic> genericsToInsert, Generic oldPrimary, Generic newPrimary) {
		Generic updated = replace(genericsToInsert.next(), (GenericImpl) oldPrimary, (GenericImpl) newPrimary);
		while (genericsToInsert.hasNext())
			replace(genericsToInsert.next(), (GenericImpl) oldPrimary, (GenericImpl) newPrimary);
		return (T) updated;
	}

	// TODO KK
	private Generic replace(Generic genericToReplace, GenericImpl oldImplicit, GenericImpl newImplicit) {
		if (((GenericImpl) genericToReplace).isPrimary())
			return bindPrimaryByValue(((GenericImpl) genericToReplace).supers[0], genericToReplace.getValue(), genericToReplace.getMetaLevel());

		Generic[] interfaces = ((GenericImpl) genericToReplace).getPrimariesArray();
		Generic[] resultInterfaces = new Generic[interfaces.length];
		for (int i = 0; i < interfaces.length; i++)
			resultInterfaces[i] = ((GenericImpl) interfaces[i]).isPrimary() ? getNewPrimary(interfaces[i], oldImplicit, newImplicit) : replace(interfaces[i], oldImplicit, newImplicit);
		Generic[] components = ((GenericImpl) genericToReplace).components;
		Generic[] resultComponents = new Generic[components.length];
		for (int i = 0; i < components.length; i++)
			resultComponents[i] = genericToReplace.equals(components[i]) ? null : ((GenericImpl) components[i]).isPrimary() ? getNewPrimary(components[i], oldImplicit, newImplicit) : replace(components[i], oldImplicit, newImplicit);
		return internalBind(genericToReplace.getImplicit().equals(oldImplicit) ? newImplicit : genericToReplace.getImplicit(), resultInterfaces, resultComponents);
	}

	private Generic getNewPrimary(Generic oldSubPrimary, Generic oldPrimary, Generic newPrimary) {
		if (!(oldSubPrimary.inheritsFrom(oldPrimary)))
			return oldSubPrimary;
		if (oldSubPrimary.equals(oldPrimary))
			return newPrimary;
		return bindPrimaryByValue(getNewPrimary(((GenericImpl) oldSubPrimary).supers[0], oldPrimary, newPrimary), oldSubPrimary.getValue(), oldSubPrimary.getMetaLevel());
	}

	@Override
	public boolean isScheduledToRemove(Generic generic) {
		return getInternalContext().isScheduledToRemove(generic) || subContext.isScheduledToRemove(generic);
	}

	@Override
	public boolean isScheduledToAdd(Generic generic) {
		return getInternalContext().isScheduledToAdd(generic) || subContext.isScheduledToAdd(generic);
	}

	@Override
	public <T extends Type> T newType(Serializable value) {
		return this.<T> newSubType(value);
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> Iterator<T> allInheritingsIterator(final Context context) {
		return (Iterator<T>) new AbstractPreTreeIterator<Generic>(context.getEngine()) {

			private static final long serialVersionUID = 8161663636838488529L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return (((GenericImpl) node).directInheritingsIterator(context));
			}
		};
	}

	@Override
	public <T extends Type> Snapshot<T> getTypes() {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return CacheImpl.this.<T> allInheritingsIterator(CacheImpl.this);
			}
		};
	}

	@Override
	public <T extends Type> T getType(final Serializable value) {
		return this.<T> getTypes().filter(new Filter<T>() {
			@Override
			public boolean isSelected(T element) {
				return Objects.equals(element.getValue(), value);
			}

		}).first();
	}

	@Override
	public <T extends Type> T newSubType(Serializable value, Type... superTypes) {
		return newSubType(value, superTypes, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T newSubType(Serializable value, Type[] superTypes, Generic... components) {
		return bind(bindPrimaryByValue(getEngine(), value, SystemGeneric.STRUCTURAL), superTypes, components);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value) {
		return newTree(value, 1);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value, int dim) {
		return this.<T> bind(bindPrimaryByValue(getEngine(), value, SystemGeneric.STRUCTURAL), Statics.EMPTY_GENERIC_ARRAY, new Generic[dim]).<T> disableInheritance(this);
	}

	@Override
	public Cache newSuperCache() {
		return this.<EngineImpl> getEngine().getFactory().newCache(this);
	}

	<T extends Generic> NavigableSet<T> orderAndRemoveDependencies(final T old) {
		NavigableSet<T> orderedGenerics = orderDependencies(old);
		for (T generic : orderedGenerics.descendingSet())
			remove(generic);
		return orderedGenerics;
	}

	// @Override
	// @SuppressWarnings("unchecked")
	// TODO KK
	// public <T extends Generic> T reFind(Generic generic) {
	// if (generic.isEngine())
	// return getEngine();
	// if (generic.isAlive(this))
	// return (T) generic;
	// if (((GenericImpl) generic).isPrimary())
	// return findPrimaryByValue(reFind(((GenericImpl) generic).supers[0]), generic.getValue(), generic.getMetaLevel());
	// Generic[] primariesArray = ((GenericImpl) generic).getPrimariesArray();
	// Generic[] boundPrimaries = new Generic[primariesArray.length];
	// for (int i = 0; i < primariesArray.length; i++)
	// boundPrimaries[i] = reFind(((GenericImpl) primariesArray[i]));
	// Generic[] extendedComponents = ((GenericImpl) generic).components;
	// // TODO KK
	// // for (int i = 0; i < extendedComponents.length; i++)
	// // extendedComponents[i] = generic.equals(extendedComponents[i]) ? null : reFind(extendedComponents[i]);
	// Generic[] directSupers = getDirectSupers(boundPrimaries, extendedComponents);
	// for (int i = 0; i < directSupers.length; i++)
	// if (((GenericImpl) directSupers[i]).equiv(boundPrimaries, extendedComponents))
	// return (T) directSupers[i];
	// throw new IllegalStateException();
	// }

	// TODO refactor => subtype complexe
	<T extends Generic> T bind(Class<?> clazz) {
		// L'odre de construction des Generic est important pour que l'implicit soit toujours en dernier.
		// Il faut donc appeler le findSupers avant le bindPrimaryByValue.
		Generic[] supers = findSupers(clazz);
		return bind(bindPrimaryByValue(findImplicitSuper(clazz), findImplictValue(clazz), findMetaLevel(clazz)), supers, findComponents(clazz));
	}

	public <T extends Generic> T bind(Generic implicit, Generic[] supers, Generic[] components) {
		return internalBind(implicit, Statics.insertFirstIntoArray(implicit, supers), components);
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T internalBind(Generic implicit, Generic[] supers, Generic[] components) {
		final Generic[] interfaces = new Primaries(supers).toArray();
		Generic[] directSupers = getDirectSupers(interfaces, components);
		if (directSupers.length == 1 && ((GenericImpl) directSupers[0]).equiv(interfaces, components)) {
			if (!implicit.equals(directSupers[0].getImplicit()))
				throw new IllegalSelectorException();
			return (T) directSupers[0];
		}

		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		for (Generic directSuper : directSupers) {
			Iterator<Generic> removeIterator = concernedDependenciesIterator(directSuper, interfaces, components);
			while (removeIterator.hasNext())
				orderedDependencies.addAll(orderDependencies(removeIterator.next()));
		}
		for (Generic generic : orderedDependencies.descendingSet())
			remove(generic);

		Generic newGeneric = ((GenericImpl) this.<EngineImpl> getEngine().getFactory().newGeneric()).initializeComplex(implicit, directSupers, components);
		T superGeneric = this.<T> insert(newGeneric);
		new ConnectionMap().reBuild(orderedDependencies);
		// assert superGeneric == findByDirectSupers(((GenericImpl) newGeneric).supers, components);
		return superGeneric;
	}

	// TODO clean
	// @SuppressWarnings("unchecked")
	// private <T extends Generic> T findByDirectSupers(Generic[] directSupers, Generic[] components) {
	// Iterator<Generic> iterator = components.length > 0 && components[0] != null ? compositesIterator(components[0]) : directInheritingsIterator(directSupers[0]);
	// while (iterator.hasNext()) {
	// Generic directInheriting = iterator.next();
	// if (Arrays.equals(((GenericImpl) directInheriting).supers, directSupers) && Arrays.equals(((GenericImpl) directInheriting).components, ((GenericImpl) directInheriting).transform(components)))
	// return (T) directInheriting;
	// }
	// return null;
	// }

	private Iterator<Generic> concernedDependenciesIterator(Generic directSuper, final Generic[] interfaces, final Generic[] extendedComponents) {
		return new AbstractFilterIterator<Generic>(directInheritingsIterator(directSuper)) {
			@Override
			public boolean isSelected() {
				return !((GenericImpl) next).isPhantom() && GenericImpl.isSuperOf(interfaces, extendedComponents, ((GenericImpl) next).getPrimariesArray(), ((GenericImpl) next).components);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T reBind(final Generic generic) {
		ConnectionMap connectionMap = new ConnectionMap();
		connectionMap.reBuild(orderAndRemoveDependencies(generic));
		T rebind = (T) connectionMap.get(generic);
		// TODO clean
		// assert rebind == (((GenericImpl) rebind).isPrimary() ? findPrimaryByValue(((GenericImpl) rebind).supers[0], rebind.getValue(), rebind.getMetaLevel()) : findByDirectSupers(((GenericImpl) rebind).supers, ((GenericImpl) rebind).components));
		return rebind;
	}

	private class ConnectionMap extends HashMap<Generic, Generic> {
		private static final long serialVersionUID = 8257917150315417734L;

		// TODO KK getDirectSupers
		private void reBuild(NavigableSet<Generic> orderedDependencies) {
			for (Generic orderedDependency : orderedDependencies) {
				Generic[] newComponents = adjust(((GenericImpl) orderedDependency).components);
				Generic[] directSupers = ((GenericImpl) orderedDependency).isPrimary() ? adjust(((GenericImpl) orderedDependency).supers[0]) : getDirectSupers(adjust(((GenericImpl) orderedDependency).getPrimariesArray()), newComponents);
				// Generic[] directSupers = adjust(((GenericImpl) orderedDependency).supers);
				Generic bind = insert(((GenericImpl) CacheImpl.this.<EngineImpl> getEngine().getFactory().newGeneric()).initializeComplex(orderedDependency.getImplicit(), directSupers, newComponents));
				put(orderedDependency, bind);
			}
		}

		private Generic[] adjust(Generic... oldComponents) {
			Generic[] newComponents = new Generic[oldComponents.length];
			for (int i = 0; i < newComponents.length; i++) {
				Generic newComponent = get(oldComponents[i]);
				newComponents[i] = newComponent == null ? oldComponents[i] : newComponent;
			}
			return newComponents;
		}
	}

	protected void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				find(dependencyClass);
	}

	public class InternalCache extends InternalContext<CacheImpl> {

		private static final long serialVersionUID = 21372907392620336L;

		protected final Set<Generic> adds = new LinkedHashSet<Generic>();
		protected final Set<Generic> removes = new LinkedHashSet<Generic>();

		public void flush() throws AbstractConstraintViolationException, ConcurrencyControlException {
			getSubContext().getInternalContext().apply(adds, removes);
		}

		@Override
		protected void add(GenericImpl generic) {
			adds.add(generic);
			super.add(generic);
		}

		@Override
		protected void remove(GenericImpl generic) {
			boolean result = removes.add(generic);
			assert result == true;
			super.remove(generic);
		}

		@Override
		protected void cancelAdd(GenericImpl generic) {
			boolean result = adds.remove(generic);
			assert result == true;
			super.cancelAdd(generic);
		}

		@Override
		protected void cancelRemove(GenericImpl generic) {
			boolean result = removes.remove(generic);
			assert result == true;
			super.cancelRemove(generic);
		}

		public void addGeneric(Generic generic) throws AbstractConstraintViolationException {
			add((GenericImpl) generic);
			checkConsistency(CheckingType.CHECK_ON_ADD_NODE, true, Arrays.asList(generic));
			checkConstraints(CheckingType.CHECK_ON_ADD_NODE, true, Arrays.asList(generic));
		}

		public void addGenericWithoutCheck(Generic generic) throws AbstractConstraintViolationException {
			add((GenericImpl) generic);
		}

		public void removeGeneric(Generic generic) throws AbstractConstraintViolationException {
			removeOrCancelAdd(generic);
			checkConsistency(CheckingType.CHECK_ON_REMOVE_NODE, true, Arrays.asList(generic));
			checkConstraints(CheckingType.CHECK_ON_REMOVE_NODE, true, Arrays.asList(generic));
		}

		public void removeGenericWithoutCheck(Generic generic) throws AbstractConstraintViolationException {
			removeOrCancelAdd(generic);
		}

		public void removeOrCancelAdd(Generic generic) throws AbstractConstraintViolationException {
			if (adds.contains(generic))
				cancelAdd((GenericImpl) generic);
			else
				remove((GenericImpl) generic);
		}

		public boolean isAlive(Generic generic) {
			return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
		}

		public boolean isScheduledToRemove(Generic generic) {
			return removes.contains(generic);
		}

		public boolean isScheduledToAdd(Generic generic) {
			return adds.contains(generic);
		}

		public void checkConstraints() throws AbstractConstraintViolationException {
			checkConstraints(adds, removes);
		}
	}

	static class CacheDependencies implements TimestampedDependencies {

		private transient TimestampedDependencies underlyingDependencies;

		private PseudoConcurrentSnapshot inserts = new PseudoConcurrentSnapshot();
		private PseudoConcurrentSnapshot deletes = new PseudoConcurrentSnapshot();

		public CacheDependencies(TimestampedDependencies underlyingDependencies) {
			assert underlyingDependencies != null;
			this.underlyingDependencies = underlyingDependencies;
		}

		@Override
		public void add(Generic generic) {
			inserts.add(generic);
		}

		@Override
		public void remove(Generic generic) {
			if (!inserts.remove(generic))
				deletes.add(generic);
		}

		@Override
		public Iterator<Generic> iterator(long ts) {
			return new InternalIterator(underlyingDependencies.iterator(ts));
		}

		private class InternalIterator extends AbstractAwareIterator<Generic> implements Iterator<Generic> {
			private Iterator<Generic> underlyingIterator;
			private Iterator<Generic> insertsIterator = inserts.iterator();

			private InternalIterator(Iterator<Generic> underlyingIterator) {
				this.underlyingIterator = underlyingIterator;
			}

			@Override
			protected void advance() {
				while (underlyingIterator.hasNext()) {
					Generic generic = underlyingIterator.next();
					if (!deletes.contains(generic)) {
						next = generic;
						return;
					}
				}
				while (insertsIterator.hasNext()) {
					next = insertsIterator.next();
					return;
				}
				next = null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

}
