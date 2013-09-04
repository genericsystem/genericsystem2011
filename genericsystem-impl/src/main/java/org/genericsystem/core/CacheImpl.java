package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic.ExtendedMap;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.FunctionalConsistencyViolationException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractAwareIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintValue;
import org.genericsystem.snapshot.PseudoConcurrentSnapshot;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.genericsystem.systemproperties.constraints.AbstractConstraintImpl;
import org.genericsystem.systemproperties.constraints.AbstractConstraintImpl.CheckingType;
import org.genericsystem.tree.TreeImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class CacheImpl extends AbstractContext implements Cache {
	private static final long serialVersionUID = 6124326077696104707L;

	private AbstractContext subContext;

	private transient Map<Generic, TimestampedDependencies> compositeDependenciesMap;
	private transient Map<Generic, TimestampedDependencies> inheritingDependenciesMap;

	private Set<Generic> adds;
	private Set<Generic> removes;

	public CacheImpl(Cache cache) {
		subContext = (CacheImpl) cache;
		clear();
	}

	public CacheImpl(Engine engine) {
		subContext = new Transaction(engine);
		clear();
	}

	@Override
	public void clear() {
		compositeDependenciesMap = new HashMap<>();
		inheritingDependenciesMap = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T insert(Generic generic) throws RollbackException {
		try {
			addGeneric(generic);
			return (T) generic;
		} catch (ConstraintViolationException e) {
			rollback(e);
		}
		throw new IllegalStateException();// Unreachable;
	}

	@Override
	public Cache start() {
		return this.<EngineImpl> getEngine().start(this);
	}

	<T extends Generic> T bindPrimaryByValue(Generic primaryAncestor, Serializable value, int metaLevel, boolean automatic, Class<?> specializeGeneric) {
		T implicit = findPrimaryByValue(primaryAncestor, value, metaLevel);
		return implicit != null ? implicit : this.<T> insert(((GenericImpl) getEngine().getFactory().newGeneric(specializeGeneric)).initializePrimary(value, metaLevel, new Generic[] { primaryAncestor }, Statics.EMPTY_GENERIC_ARRAY, automatic));
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

	@Override
	public boolean isRemovable(Generic generic) {
		try {
			orderRemoves(generic);
		} catch (ReferentialIntegrityConstraintViolationException e) {
			return false;
		}
		return true;
	}

	void removeWithAutomatics(Generic generic) throws RollbackException {
		if (generic.getClass().isAnnotationPresent(SystemGeneric.class))
			throw new IllegalStateException();
		remove(generic);
		Generic automatic = findAutomaticAlone(generic);
		if (null != automatic)
			remove(automatic);
	}

	private void remove(Generic generic) throws RollbackException {
		try {
			internalRemove(generic);
		} catch (ConstraintViolationException e) {
			rollback(e);
		}
	}

	private Generic findAutomaticAlone(Generic generic) {
		Generic automaticCandidate = generic.getImplicit();
		if (automaticCandidate.isAlive() && automaticCandidate.isAutomatic() && automaticCandidate.getInheritings().isEmpty() && automaticCandidate.getComposites().isEmpty())
			return automaticCandidate;
		return null;
	}

	private void internalRemove(Generic node) throws ConstraintViolationException {
		if (!isAlive(node))
			throw new AliveConstraintViolationException(node + " is not alive");
		for (Generic generic : orderRemoves(node).descendingSet())
			removeGeneric(generic);
	}

	private abstract class UnsafeCacheManager<T> {
		private UnsafeCache unsafeCache = new UnsafeCache(CacheImpl.this);

		T doWork() {
			unsafeCache.start();
			T result = internalWork(unsafeCache);
			unsafeCache.flush();
			start();
			return result;
		}

		abstract T internalWork(UnsafeCache unsafeCache);
	}

	<T extends Generic> T updateValue(final Generic old, final Serializable value) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) {
				return unsafeCache.updateValue(old, value);
			}
		}.doWork();
	}

	<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) {
				return unsafeCache.addComponent(old, newComponent, pos);
			}
		}.doWork();
	}

	<T extends Generic> T removeComponent(final Generic old, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) {
				return unsafeCache.removeComponent(old, pos);
			}
		}.doWork();
	}

	<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) {
				return unsafeCache.addSuper(old, newSuper);
			}
		}.doWork();
	}

	<T extends Generic> T removeSuper(final Generic old, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) {
				return unsafeCache.removeSuper(old, pos);
			}
		}.doWork();
	}

	private class ConnectionMap extends HashMap<Generic, Generic> {
		private static final long serialVersionUID = 8257917150315417734L;

		private ConnectionMap reBind(Set<Generic> orderedDependencies, boolean computeDirectSupers) {
			for (Generic orderedDependency : orderedDependencies) {
				Generic generic;
				if (((GenericImpl) orderedDependency).isPrimary())
					generic = bindPrimaryByValue(adjust(((GenericImpl) orderedDependency).supers)[0], orderedDependency.getValue(), orderedDependency.getMetaLevel(), orderedDependency.isAutomatic(), orderedDependency.getClass());
				else {
					generic = buildAndInsertComplex(orderedDependency.getClass(), adjust(orderedDependency.getImplicit())[0],
							computeDirectSupers ? getDirectSupers(adjust(((GenericImpl) orderedDependency).getPrimariesArray()), adjust(((GenericImpl) orderedDependency).components)) : adjust(((GenericImpl) orderedDependency).supers),
							adjust(((GenericImpl) orderedDependency).components), orderedDependency.isAutomatic());
				}
				put(orderedDependency, generic);
			}
			return this;
		}

		private Generic[] adjust(Generic... oldComponents) {
			Generic[] newComponents = new Generic[oldComponents.length];
			for (int i = 0; i < newComponents.length; i++) {
				Generic newComponent = get(oldComponents[i]);
				assert newComponent == null ? isAlive(oldComponents[i]) : !isAlive(oldComponents[i]) : newComponent + " / " + oldComponents[i].info();
				newComponents[i] = newComponent == null ? oldComponents[i] : newComponent;
				assert isAlive(newComponents[i]);
			}
			return newComponents;
		}
	}

	public <T extends Generic> T reBind(Generic generic) {
		return updateValue(generic, generic.getValue());
	}

	Generic[] reBind(Generic[] generics) {
		Generic[] reBind = new Generic[generics.length];
		for (int i = 0; i < generics.length; i++) {
			Generic generic = generics[i];
			reBind[i] = generic.isAlive() ? generic : updateValue(generic, generic.getValue());
		}
		return reBind;
	}

	@Override
	public void flush() throws RollbackException {
		assert equals(getEngine().getCurrentCache());
		Exception cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				checkConstraints();
				getSubContext().apply(new Iterable<Generic>() {
					@Override
					public Iterator<Generic> iterator() {
						return new AbstractFilterIterator<Generic>(adds.iterator()) {
							@Override
							public boolean isSelected() {
								return isFlushable(next);
							}
						};
					}
				}, removes);

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
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
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
	public boolean isScheduledToRemove(Generic generic) {
		return removes.contains(generic) || subContext.isScheduledToRemove(generic);
	}

	@Override
	public boolean isScheduledToAdd(Generic generic) {
		return adds.contains(generic) || subContext.isScheduledToAdd(generic);
	}

	@Override
	public <T extends Type> T newType(Serializable value) {
		return this.<T> newSubType(value);
	}

	@Override
	public <T extends Type> T getType(final Serializable value) {
		return getEngine().getSubType(value);
	}

	@Override
	public <T extends Type> T newSubType(Serializable value, Type... userSupers) {
		return newSubType(value, userSupers, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T newSubType(Serializable value, Type[] userSupers, Generic... components) {
		T result = bind(bindPrimaryByValue(getEngine(), value, SystemGeneric.STRUCTURAL, !isManuel(userSupers, components, SystemGeneric.STRUCTURAL), Generic.class), userSupers, components, false, null, false);
		assert Objects.equals(value, result.getValue());
		return result;
	}

	@Override
	public <T extends Tree> T newTree(Serializable value) {
		return newTree(value, 1);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value, int dim) {
		return this.<T> bind(bindPrimaryByValue(getEngine(), value, SystemGeneric.STRUCTURAL, true, TreeImpl.class), new Generic[] { find(NoInheritanceSystemType.class) }, new Generic[dim], false, TreeImpl.class, false);// .<T> disableInheritance();
	}

	@Override
	public Cache newSuperCache() {
		return this.<EngineImpl> getEngine().getFactory().newCache(this);
	}

	<T extends Generic> NavigableSet<T> orderAndRemoveDependencies(final T old) {
		NavigableSet<T> orderedGenerics = orderDependencies(old);
		for (T generic : orderedGenerics.descendingSet())
			if (generic.isAlive())
				remove(generic);
		return orderedGenerics;
	}

	// TODO KK findImplicitSuper
	<T extends Generic> T bind(Class<?> clazz) {
		Class<?> specialize = Generic.class;
		if (clazz.getSuperclass().equals(GenericImpl.class))
			specialize = clazz;
		Generic[] components = findComponents(clazz);
		// TODO return not Engine
		Generic[] userSupers = findUserSupers(clazz);
		assert userSupers.length >= 0;
		int metaLevel = findMetaLevel(clazz);
		return bind(bindPrimaryByValue(findImplicitSuper(clazz), findImplictValue(clazz), metaLevel, !isManuel(userSupers, components, metaLevel), specialize), userSupers, components, false, clazz, false);
	}

	boolean isManuel(Generic[] userSupers, Generic[] components, int metaLevel) {
		return metaLevel == SystemGeneric.CONCRETE || (components.length == 0 && (userSupers.length == 0 || (userSupers.length == 1 && userSupers[0].isEngine())));
	}

	<T extends Generic> T bind(Class<?> specializationClass, Generic implicit, boolean automatic, Generic directSuper, boolean existsException, Generic... components) {
		if (implicit.isConcrete()) {
			components = ((GenericImpl) directSuper).sortAndCheck(components);
			Generic meta = directSuper.getMetaLevel() == implicit.getMetaLevel() ? directSuper.getMeta() : directSuper;
			InstanceGenericClass instanceClass = meta.getClass().getAnnotation(InstanceGenericClass.class);
			if (instanceClass != null)
				specializationClass = instanceClass.value();
		}
		return bind(implicit, new Generic[] { directSuper }, components, automatic, specializationClass, existsException);
	}

	<T extends Generic> T bind(Generic implicit, Generic[] supers, Generic[] components, boolean automatic, Class<?> specializationClass, boolean existsException) {
		final Primaries primaries = new Primaries(supers);
		primaries.add(implicit);
		Generic[] interfaces = primaries.toArray();
		if (implicit.getValue() != null) {
			Generic phantomImplicit = findPrimaryByValue(((GenericImpl) implicit).supers[0], null, implicit.getMetaLevel());
			if (phantomImplicit != null) {
				primaries.add(phantomImplicit);
				T phantom = fastFindByInterfaces(phantomImplicit, primaries.toArray(), components);
				if (phantom != null)
					phantom.remove();
			}
		}
		T result = fastFindByInterfaces(implicit, interfaces, components);
		if (result != null) {
			if (!Objects.equals(result.getValue(), implicit.getValue()))
				rollback(new FunctionalConsistencyViolationException(result.info()));
			if (existsException)
				rollback(new ExistsException(result + " already exists !"));
			return result;
		}
		return internalBind(implicit, interfaces, components, automatic, specializationClass);
	}

	private <T extends Generic> T internalBind(Generic implicit, Generic[] interfaces, Generic[] components, boolean automatic, Class<?> specializeGeneric) {
		assert implicit.isAlive();
		Generic[] directSupers = getDirectSupers(interfaces, components);
		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		for (Generic directSuper : directSupers) {
			Iterator<Generic> removeIterator = concernedDependenciesIterator(directSuper, interfaces, components);
			while (removeIterator.hasNext())
				orderedDependencies.addAll(orderDependencies(removeIterator.next()));
		}
		for (Generic generic : orderedDependencies.descendingSet())
			simpleRemove(generic);

		ConnectionMap connectionMap = new ConnectionMap();
		// if (!implicit.isAlive()) {
		// Generic newImplicit = bindPrimaryByValue(((GenericImpl) implicit).supers[0], implicit.getValue(), implicit.getMetaLevel(), implicit.isAutomatic(), implicit.getClass());
		// connectionMap.put(implicit, newImplicit);
		// implicit = newImplicit;
		// directSupers = connectionMap.adjust(directSupers);
		// }
		Generic newGeneric = ((GenericImpl) this.<EngineImpl> getEngine().getFactory().newGeneric(specializeGeneric)).initializeComplex(implicit, directSupers, components, automatic);
		T superGeneric = this.<T> insert(newGeneric);
		connectionMap.reBind(orderedDependencies, true);
		return superGeneric;
	}

	<T extends Generic> Iterator<T> concernedDependenciesIterator(final Generic directSuper, final Generic[] interfaces, final Generic[] components) {
		return new AbstractFilterIterator<T>(this.<T> directInheritingsIterator(directSuper)) {
			@Override
			public boolean isSelected() {
				return /* next.getValue() != null && */GenericImpl.isSuperOf(interfaces, components, ((GenericImpl) next).getPrimariesArray(), ((GenericImpl) next).components);
			}
		};
	}

	private <T extends Generic> T buildAndInsertComplex(Class<?> clazz, Generic implicit, Generic[] supers, Generic[] components, boolean automatic) {
		return insert(this.<EngineImpl> getEngine().buildComplex(clazz, implicit, supers, components, automatic));
	}

	protected void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				find(dependencyClass);
	}

	@SuppressWarnings("unchecked")
	protected void checkConsistency(CheckingType checkingType, boolean isFlushTime, Iterable<Generic> generics) throws ConstraintViolationException {
		Generic constraintValue = find(ConstraintValue.class);
		for (Serializable key : getEngine().getContraintsMap().keySet())
			for (Generic generic : generics)
				if (generic.isInstanceOf(constraintValue)) {
					AbstractConstraintImpl constraint = find(((AxedPropertyClass<GenericImpl>) key).getClazz());
					if (isCheckable(constraint, generic, checkingType, isFlushTime)) {
						GenericImpl base = ((GenericImpl) generic).<GenericImpl> getBaseComponent().<GenericImpl> getBaseComponent().<GenericImpl> getBaseComponent();
						if (null != base)
							constraint.checkConsistency(base, (Holder) generic, base, (AxedPropertyClass<GenericImpl>) ((GenericImpl) generic).<GenericImpl> getBaseComponent().getValue());
					}
				}
	}

	protected void checkConstraints(Iterable<Generic> adds, Iterable<Generic> removes) throws ConstraintViolationException {
		checkConsistency(CheckingType.CHECK_ON_ADD_NODE, true, adds);
		checkConsistency(CheckingType.CHECK_ON_REMOVE_NODE, true, removes);
		checkConstraints(CheckingType.CHECK_ON_ADD_NODE, true, adds);
		checkConstraints(CheckingType.CHECK_ON_REMOVE_NODE, true, removes);
	}

	private void checkConstraints(CheckingType checkingType, boolean isFlushTime, Iterable<Generic> generics) throws ConstraintViolationException {
		for (Generic generic : generics) {
			ExtendedMap<Serializable, Serializable> constraintMap = generic.getContraintsMap();
			for (Serializable key : constraintMap.keySet()) {
				Holder valueHolder = constraintMap.getValueHolder(key);
				AbstractConstraintImpl keyHolder = valueHolder.getBaseComponent();
				if (isCheckable(keyHolder, generic, checkingType, isFlushTime))
					keyHolder.check(generic, valueHolder);
			}
		}
	}

	protected boolean isCheckable(AbstractConstraintImpl constraint, Generic generic, CheckingType checkingType, boolean isFlushTime) {
		return (isFlushTime || constraint.isImmediatelyCheckable()) && constraint.isCheckedAt(generic, checkingType);
	}

	@Override
	void simpleAdd(Generic generic) {
		adds.add(generic);
		super.simpleAdd(generic);
	}

	@Override
	void simpleRemove(Generic generic) {
		if (adds.contains(generic))
			adds.remove(generic);
		else
			removes.add(generic);
		super.simpleRemove(generic);
	}

	private void addGeneric(Generic generic) throws ConstraintViolationException {
		simpleAdd(generic);
		checkConsistency(CheckingType.CHECK_ON_ADD_NODE, false, Arrays.asList(generic));
		checkConstraints(CheckingType.CHECK_ON_ADD_NODE, false, Arrays.asList(generic));
	}

	private void removeGeneric(Generic generic) throws ConstraintViolationException {
		simpleRemove(generic);
		checkConsistency(CheckingType.CHECK_ON_REMOVE_NODE, false, Arrays.asList(generic));
		checkConstraints(CheckingType.CHECK_ON_REMOVE_NODE, false, Arrays.asList(generic));
	}

	private void checkConstraints() throws ConstraintViolationException {
		checkConstraints(adds, removes);
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

	@Override
	public Snapshot<Type> getAllTypes() {
		return getEngine().getAllInstances();
	}

	// public <T extends Generic> Iterator<T> queryIterator(Context context, final int levelFilter, Generic[] supers, final Generic... components) {
	// final Primaries primaries = new Primaries(supers);
	// final Generic[] interfaces = primaries.toArray();
	// // Generic[] directSupers = getDirectSupers(interfaces, components);
	// // assert directSupers.length >= 2;
	// return new AbstractConcateIterator<Generic, T>(getDirectSupersIterator(interfaces, components)) {
	// @Override
	// protected Iterator<T> getIterator(Generic directSuper) {
	// return new AbstractFilterIterator<T>(CacheImpl.this.<T> concernedDependenciesIterator(directSuper, interfaces, components)) {
	// @Override
	// public boolean isSelected() {
	// return levelFilter == next.getMetaLevel();
	// }
	// };
	// }
	// };
	// }
	static class UnsafeCache extends CacheImpl {
		private static final long serialVersionUID = 4843334203915625618L;

		public UnsafeCache(Cache cache) {
			super(cache);
		}

		public UnsafeCache(Engine engine) {
			super(engine);
		}

		@Override
		protected boolean isCheckable(AbstractConstraintImpl constraint, Generic generic, CheckingType checkingType, boolean isFlushTime) {
			return isFlushTime && constraint.isCheckedAt(generic, checkingType);
		}

		@Override
		<T extends Generic> T updateValue(final Generic old, final Serializable value) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					Generic newImplicit = bindPrimaryByValue(reBind(old.<GenericImpl> getImplicit().supers)[0], value, old.getMetaLevel(), old.getImplicit().isAutomatic(), old.getClass());
					if (((GenericImpl) old).isPrimary())
						return newImplicit;
					return bind(newImplicit, Statics.replace(0, reBind(((GenericImpl) old).supers), newImplicit), reBind(((GenericImpl) old).selfToNullComponents()), old.isAutomatic(), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					// TODO KK
					if (((GenericImpl) old).isPrimary()) {
						Generic newPrimary = bindPrimaryByValue(old.<GenericImpl> getImplicit().supers[0], old.getValue(), old.getMetaLevel(), true, old.getClass());
						return bind(newPrimary, Statics.replace(0, ((GenericImpl) old).supers, newPrimary), Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos), old.isAutomatic(), old.getClass(), true);
					}
					return bind(old.getImplicit(), ((GenericImpl) old).supers, Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos), old.isAutomatic(), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T removeComponent(final Generic old, final int pos) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(old.getImplicit(), ((GenericImpl) old).supers, Statics.truncate(pos, ((GenericImpl) old).selfToNullComponents()), old.isAutomatic(), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(old.getImplicit(), Statics.insertLastIntoArray(newSuper, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.isAutomatic(), old.getClass(), true);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T removeSuper(final Generic old, final int pos) {
			if (pos == 0)
				throw new UnsupportedOperationException();
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(old.getImplicit(), Statics.truncate(pos, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.isAutomatic(), old.getClass(), true);
				}
			}.rebuildAll(old);
		}

		abstract class Restructurator {
			@SuppressWarnings("unchecked")
			<T extends Generic> T rebuildAll(Generic old) {
				NavigableSet<Generic> dependencies = orderAndRemoveDependencies(old);
				dependencies.remove(old);
				ConnectionMap map = new ConnectionMap();
				map.put(old, rebuild());
				return (T) map.reBind(dependencies, false).get(old);
			}

			abstract Generic rebuild();
		}

	}
}
