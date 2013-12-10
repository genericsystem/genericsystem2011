package org.genericsystem.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractAxedConstraintImpl;
import org.genericsystem.constraints.AbstractConstraintImpl.CheckingType;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.NotRemovableException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractAwareIterator;
import org.genericsystem.map.AbstractMapProvider.AbstractExtendedMap;
import org.genericsystem.map.AxedPropertyClass;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintValue;
import org.genericsystem.snapshot.PseudoConcurrentSnapshot;
import org.genericsystem.systemproperties.MetaAttribute;
import org.genericsystem.systemproperties.MetaRelation;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.genericsystem.tree.TreeImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class CacheImpl extends AbstractContext implements Cache {

	protected AbstractContext subContext;

	private transient Map<Generic, TimestampedDependencies> compositeDependenciesMap;
	private transient Map<Generic, TimestampedDependencies> inheritingDependenciesMap;

	protected Set<Generic> adds;
	protected Set<Generic> removes;
	protected Set<Generic> automatics;

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
		automatics = new LinkedHashSet<>();
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T insert(Generic generic, boolean automatic) throws RollbackException {
		try {
			addGeneric(generic, automatic);
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

	@Override
	public void stop() {
		this.<EngineImpl> getEngine().stop(this);
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

	public void refresh() throws ConstraintViolationException {
		pickNewTs();
		try {
			checkConstraints();
		} catch (ConstraintViolationException e) {
			rollback(e);
			throw e; // re-throw the same exception
		}
	}

	@Override
	public boolean isRemovable(Generic generic) {
		try {
			orderDependenciesForRemove(generic);
		} catch (ReferentialIntegrityConstraintViolationException e) {
			return false;
		}
		return true;
	}

	private <T extends Generic> NavigableSet<T> orderAndRemoveDependenciesForRemove(final T old) throws RollbackException {
		try {
			NavigableSet<T> orderedGenerics = orderDependenciesForRemove(old);
			for (Generic generic : orderDependenciesForRemove(old).descendingSet())
				removeGeneric(generic);
			return orderedGenerics;
		} catch (ConstraintViolationException e) {
			rollback(e);
			return null;
		}
	}

	private NavigableSet<Generic> orderAndRemoveDependencies(final Generic old) {
		NavigableSet<Generic> orderedGenerics = orderDependencies(old);
		for (Generic dependency : orderedGenerics.descendingSet())
			simpleRemove(dependency);
		return orderedGenerics;
	}

	void remove(final Generic generic, final RemoveStrategy removeStrategy) throws RollbackException {
		switch (removeStrategy) {
		case NORMAl:
			orderAndRemoveDependenciesForRemove(generic);
		break;
		case CONSERVE:
			new Restructurator() {
				private static final long serialVersionUID = 7326023526567814490L;

				@Override
				Generic rebuild() {
					return null;
				}
			}.rebuildAll(generic, Statics.MULTIDIRECTIONAL);
		case FORCE:
			orderAndRemoveDependencies(generic);
		break;
		case PROJECT:
			((GenericImpl) generic).project();
			remove(generic, RemoveStrategy.CONSERVE);
		break;
		}
	}

	<T extends Generic> T setValue(final Generic old, final Serializable value) {
		return new Restructurator() {
			private static final long serialVersionUID = -8740556842703459056L;

			@Override
			Generic rebuild() {
				Generic meta = old.getMeta();
				HomeTreeNode homeTreeNode = ((GenericImpl) meta).bindInstanceNode(value);
				return bindDependency(meta, homeTreeNode, ((GenericImpl) old).supers, ((GenericImpl) old).selfToNullComponents(), old.getClass(), Statics.MULTIDIRECTIONAL, false, isAutomatic(old));
			}
		}.rebuildAll(old, Statics.MULTIDIRECTIONAL);
	}

	<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
		return new Restructurator() {
			private static final long serialVersionUID = 8721991478541508274L;

			@Override
			Generic rebuild() {
				return bindDependency(old.getMeta(), ((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos), old.getClass(), Statics.MULTIDIRECTIONAL,
						false, isAutomatic(old));
			}
		}.rebuildAll(old, Statics.MULTIDIRECTIONAL);
	}

	<T extends Generic> T removeComponent(final Generic old, final int pos) {
		return new Restructurator() {
			private static final long serialVersionUID = 56550167779119933L;

			@Override
			Generic rebuild() {
				return bindDependency(old.getMeta(), ((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.truncate(pos, ((GenericImpl) old).selfToNullComponents()), old.getClass(), Statics.MULTIDIRECTIONAL, false, isAutomatic(old));
			}
		}.rebuildAll(old, Statics.MULTIDIRECTIONAL);
	}

	<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
		return new Restructurator() {
			private static final long serialVersionUID = -8032263893165253991L;

			@Override
			Generic rebuild() {
				return bindDependency(old.getMeta(), ((GenericImpl) old).getHomeTreeNode(), Statics.insertLastIntoArray(newSuper, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), Statics.MULTIDIRECTIONAL, true,
						isAutomatic(old));
			}
		}.rebuildAll(old, Statics.MULTIDIRECTIONAL);
	}

	<T extends Generic> T removeSuper(final Generic old, final int pos) {
		if (pos == 0 && ((GenericImpl) old).supers.length == 1)
			rollback(new UnsupportedOperationException());
		return new Restructurator() {
			private static final long serialVersionUID = -1477153574889495455L;

			@Override
			Generic rebuild() {
				return bindDependency(old.getMeta(), ((GenericImpl) old).getHomeTreeNode(), Statics.truncate(pos, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), Statics.MULTIDIRECTIONAL, true, isAutomatic(old));
			}
		}.rebuildAll(old, Statics.MULTIDIRECTIONAL);
	}

	public <T extends Generic> T reBind(Generic generic) {
		return setValue(generic, generic.getValue());
	}

	@Override
	public boolean isAutomatic(Generic generic) {
		return automatics.contains(generic);
	};

	@Override
	public void flush() throws RollbackException {
		flush(Statics.LIFE_TIME_OUT);
	}

	public void flush(long timeOut) throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				if (getEngine().pickNewTs() - getTs() >= timeOut)
					throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIME_OUT);
				checkConstraints();
				getSubContext().apply(adds, removes);
				clear();
				return;
			} catch (ConcurrencyControlException e) {
				cause = e;
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
					pickNewTs();
				} catch (InterruptedException ex) {
					throw new IllegalStateException(ex);
				}
			} catch (Exception e) {
				rollback(e);
			}
		rollback(cause);
	}

	protected void rollback(Throwable e) throws RollbackException {
		clear();
		throw new RollbackException(e);
	}

	@Override
	public boolean isAlive(Generic generic) {
		return adds.contains(generic) || automatics.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
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

	// @Override
	// public <T extends Type> T newType(Serializable value) {
	// return this.<T> newType(value);
	// }

	@Override
	public <T extends Type> T getType(final Serializable value) {
		return getEngine().getAllSubType(value);
	}

	@Override
	public <T extends Type> T addType(Serializable name, Type... superTypes) {
		return addType(name, superTypes, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T addType(Serializable name, Type[] superTypes, Generic... components) {
		return internalSetType(name, true, superTypes, components);
	}

	@Override
	public <T extends Type> T setType(Serializable name, Type... superTypes) {
		return setType(name, superTypes, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T setType(Serializable name, Type[] superTypes, Generic... components) {
		return internalSetType(name, false, superTypes, components);
	}

	private <T extends Type> T internalSetType(Serializable name, boolean existsException, Type[] superTypes, Generic... components) {
		return internalBind(getEngine(), name, superTypes, components, null, Statics.MULTIDIRECTIONAL, false, existsException);
	}

	@Override
	public <T extends Tree> T addTree(Serializable name) {
		return addTree(name, 1);
	}

	@Override
	public <T extends Tree> T addTree(Serializable name, int dim) {
		return internalSetTree(name, dim, true);
	}

	@Override
	public <T extends Tree> T setTree(Serializable name) {
		return setTree(name, 1);
	}

	@Override
	public <T extends Tree> T setTree(Serializable name, int dim) {
		return internalSetTree(name, dim, false);
	}

	private <T extends Tree> T internalSetTree(Serializable name, int dim, boolean existsException) {
		return bind(name, TreeImpl.class, existsException, new Generic[] { find(NoInheritanceSystemType.class) }, new Generic[dim]);
	}

	private <T extends Generic> T bind(Serializable name, Class<?> specializationClass, boolean existsException, Generic[] superTypes, Generic... components) {
		return this.<T> internalBind(getEngine(), name, superTypes, components, specializationClass, Statics.MULTIDIRECTIONAL, false, existsException);
	}

	@Override
	public Cache mountNewCache() {
		return getEngine().getFactory().newCache(this).start();
	}

	@Override
	public Cache flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache) subContext).start() : this;
	}

	@Override
	public Cache discardAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache) subContext).start() : this;
	}

	<T extends Generic> T bind(Class<?> clazz) {
		Generic[] userSupers = findUserSupers(clazz);
		Generic[] components = findComponents(clazz);
		GenericImpl meta = getMeta(clazz, components);
		Serializable value = findImplictValue(clazz);
		return this.<T> internalBind(meta, value, Statics.insertFirst(meta, userSupers), components, clazz, Statics.MULTIDIRECTIONAL, false, false);
	}

	<T extends Generic> T bind(Generic meta, Serializable value, Class<?> specializationClass, Generic directSuper, boolean existsException, int axe, Generic... components) {
		Generic[] sortAndCheck = ((GenericImpl) directSuper).sortAndCheck(components);
		return internalBind(meta, value, new Generic[] { directSuper }, sortAndCheck, specializationClass, Statics.MULTIDIRECTIONAL != axe ? findAxe(sortAndCheck, components[axe]) : axe, false, existsException);
	}

	int findAxe(Generic[] sorts, Generic baseComponent) throws RollbackException {
		for (int i = 0; i < sorts.length; i++)
			if (baseComponent.equals(sorts[i]))
				return i;
		rollback(new IllegalStateException());
		return -1;// Unreachable
	}

	private GenericImpl getMeta(Class<?> clazz, Generic[] components) {
		Extends extendsAnnotation = clazz.getAnnotation(Extends.class);
		Class<?> meta = extendsAnnotation == null || Engine.class.equals(extendsAnnotation.meta()) ? EngineImpl.class : extendsAnnotation.meta();
		if (EngineImpl.class.equals(meta)) {
			if (components.length == 0 || MetaAttribute.class.equals(clazz))
				return getEngine();
			else if (components.length == 1 || MetaRelation.class.equals(clazz))
				meta = MetaAttribute.class;
			else
				meta = MetaRelation.class;
		}
		return this.<GenericImpl> find(meta);
	}

	abstract class Restructurator extends HashMap<Generic, Generic> {
		private static final long serialVersionUID = 946034598495324341L;

		<T extends Generic> T rebuildAll(Generic old, int basePos) {
			NavigableMap<Generic, Integer> dependenciesMap = orderDependencyMap(old, basePos);
			return rebuildAll(old, dependenciesMap);
		}

		<T extends Generic> T rebuildAll(Generic old, Set<Generic> directDependencies, int basePos) {
			NavigableMap<Generic, Integer> dependenciesMap = new AllDependencies(directDependencies, basePos);
			return rebuildAll(old, dependenciesMap);
		}

		@SuppressWarnings("unchecked")
		private <T extends Generic> T rebuildAll(Generic old, NavigableMap<Generic, Integer> dependenciesMap) {
			removeAll(dependenciesMap);
			Generic build = rebuild();
			if (old != null) {
				dependenciesMap.remove(old);
				put(old, build);
			}
			reBind(dependenciesMap);
			return (T) build;
		}

		private void reBindDependency(Generic dependency, int basePos) {
			HomeTreeNode newHomeTreeNode = ((GenericImpl) dependency).getHomeTreeNode();
			Generic[] supers = adjust(((GenericImpl) dependency).supers);
			Generic[] components = adjust(((GenericImpl) dependency).selfToNullComponents());
			Generic meta = adjust(((GenericImpl) dependency).getMeta())[0];
			// should we rebind automatic dependencies ?
			put(dependency, bindDependency(meta, newHomeTreeNode, supers, components, dependency.getClass(), basePos, false, isAutomatic(dependency)));
		}

		private void removeAll(NavigableMap<Generic, Integer> dependenciesMap) {
			for (Generic dependency : dependenciesMap.descendingMap().keySet()) {
				simpleRemove(dependency);
			}
		}

		private void reBind(Map<Generic, Integer> orderedDependenciesMap) {
			for (Entry<Generic, Integer> dependencyEntry : orderedDependenciesMap.entrySet())
				reBindDependency(dependencyEntry.getKey(), dependencyEntry.getValue());
		}

		private Generic[] adjust(Generic... oldComponents) {
			return new AdjustList(oldComponents).toArray();
		}

		private class AdjustList extends ArrayList<Generic> {
			private static final long serialVersionUID = -478017222010761379L;

			private AdjustList(Generic... oldComponents) {
				for (Generic oldComponent : oldComponents) {
					Generic newComponent = Restructurator.this.get(oldComponent);
					if (newComponent != null)
						add(newComponent);
					else if (isAlive(oldComponent))
						add(oldComponent);
					else
						addAll(new AdjustList(((GenericImpl) oldComponent).supers));
				}
			}

			@Override
			public Generic[] toArray() {
				return super.toArray(new Generic[size()]);
			}
		}

		<T extends Generic> T bindDependency(Generic meta, HomeTreeNode homeTreeNode, Generic[] supers, Generic[] components, Class<?> specializationClass, int basePos, boolean existsException, boolean automatic) throws RollbackException {
			return new GenericBuilder(CacheImpl.this, meta, homeTreeNode, supers, components, basePos, false).bindDependency(specializationClass, existsException, automatic);
		}

		abstract Generic rebuild();
	}

	<T extends Generic> T internalBind(Generic meta, Serializable value, Generic[] supers, Generic[] components, final Class<?> specializationClass, int basePos, final boolean automatic, boolean existsException) throws RollbackException {
		return new GenericBuilder(this, meta, ((GenericImpl) meta).bindInstanceNode(value), supers.length != 0 ? supers : new Generic[] { getEngine() }, components, basePos, true).internalBind(specializationClass, basePos, existsException, automatic);
	}

	private class AllDependencies extends TreeMap<Generic, Integer> {
		private static final long serialVersionUID = -1925554375663814593L;

		private AllDependencies(Set<Generic> directDependencies, int basePos) {
			for (Generic dependency : directDependencies)
				putAll(orderDependencyMap(dependency, basePos));
		}
	}

	protected void check(CheckingType checkingType, Iterable<Generic> generics) throws ConstraintViolationException {
		for (Generic generic : generics)
			check(checkingType, true, generic);
	}

	protected void check(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		checkConsistency(checkingType, isFlushTime, generic);
		checkConstraints(checkingType, isFlushTime, generic);
	}

	private boolean isConsistencyToCheck(CheckingType checkingType, boolean isFlushTime, Generic generic) {
		if (isConstraintActivated(generic))
			if (isFlushTime || isImmediatelyConsistencyCheckable(((AbstractConstraintImpl) ((Holder) generic).getBaseComponent())))
				return true;
		return false;
	}

	protected boolean isImmediatelyConsistencyCheckable(AbstractConstraintImpl constraint) {
		return constraint.isImmediatelyConsistencyCheckable();
	}

	protected boolean isConstraintActivated(Generic generic) {
		if (((GenericImpl) generic).getValue() != null)
			if (isConstraintValueSetting(generic))
				if (!Boolean.FALSE.equals(generic.getValue()))
					return true;
		return false;

	}

	protected boolean isConstraintValueSetting(Generic generic) {
		return generic.isInstanceOf(find(ConstraintValue.class));
	}

	protected void checkConsistency(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		if (isConsistencyToCheck(checkingType, isFlushTime, generic)) {
			AbstractConstraintImpl keyHolder = ((Holder) generic).getBaseComponent();
			int axe = keyHolder.<AxedPropertyClass> getValue().getAxe();
			Attribute constraintBase = keyHolder.<Holder> getBaseComponent().getBaseComponent();
			if (!AbstractAxedConstraintImpl.class.isAssignableFrom(keyHolder.getClass()))
				keyHolder.check(constraintBase, generic, (Holder) generic, axe);
			else {
				Type component = constraintBase.getComponent(axe);
				if (component != null)
					for (Generic instance : component.getAllInstances())
						keyHolder.check(constraintBase, instance, (Holder) generic, axe);
			}
		}
	}

	private void checkConstraints(final CheckingType checkingType, final boolean isFlushTime, final Generic generic) throws ConstraintViolationException {
		for (final Attribute attribute : ((Type) generic).getAttributes()) {
			AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) attribute).getConstraintsMap();
			PriorityConstraintMap constraints = new PriorityConstraintMap();
			for (AxedPropertyClass key : constraintMap.keySet()) {
				Holder valueHolder = constraintMap.getValueHolder(key);
				AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();
				if (isCheckable(keyHolder, attribute, checkingType, isFlushTime) && isAxedConstraint(keyHolder) && isInstanceOf(generic, attribute, ((AxedPropertyClass) keyHolder.getValue()).getAxe()))
					constraints.put(keyHolder, valueHolder);
			}
			for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet())
				entry.getKey().check(attribute, generic, entry.getValue(), ((AxedPropertyClass) entry.getKey().getValue()).getAxe());
		}
		AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) generic).getConstraintsMap();
		PriorityConstraintMap constraints = new PriorityConstraintMap();
		for (AxedPropertyClass key : constraintMap.keySet()) {
			Holder valueHolder = constraintMap.getValueHolder(key);
			AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();
			if (isCheckable(keyHolder, generic, checkingType, isFlushTime) && generic.getMetaLevel() - ((Holder) keyHolder.getBaseComponent()).getBaseComponent().getMetaLevel() >= 1)
				constraints.put(keyHolder, valueHolder);
		}

		for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet()) {
			AbstractConstraintImpl keyHolder = entry.getKey();
			int axe = ((AxedPropertyClass) keyHolder.getValue()).getAxe();
			keyHolder.check(((Holder) keyHolder.getBaseComponent()).getBaseComponent(), isAxedConstraint(keyHolder) ? ((Attribute) generic).getComponent(axe) : generic, entry.getValue(), axe);
		}
	}

	private boolean isAxedConstraint(AbstractConstraintImpl constraint) {
		return AbstractAxedConstraintImpl.class.isAssignableFrom(constraint.getClass());
	}

	private boolean isInstanceOf(Generic generic, Attribute attribute, int axe) {
		return attribute.getComponent(axe) != null && generic.isInstanceOf(attribute.getComponent(axe));
	}

	private static class PriorityConstraintMap extends TreeMap<AbstractConstraintImpl, Holder> {

		private static final long serialVersionUID = -1661589109737403438L;

		@Override
		public Comparator<? super AbstractConstraintImpl> comparator() {
			return new Comparator<AbstractConstraintImpl>() {
				@Override
				public int compare(AbstractConstraintImpl constraint, AbstractConstraintImpl compareConstraint) {
					if (constraint.getPriority() == compareConstraint.getPriority())
						return constraint.getClass().getSimpleName().compareTo(compareConstraint.getClass().getSimpleName());
					return Integer.compare(constraint.getPriority(), compareConstraint.getPriority());
				}
			};
		}

	}

	private boolean isCheckable(AbstractConstraintImpl constraint, Generic generic, CheckingType checkingType, boolean isFlushTime) {
		return (isFlushTime || isImmediatelyCheckable(constraint)) && constraint.isCheckedAt(generic, checkingType);
	}

	protected boolean isImmediatelyCheckable(AbstractConstraintImpl constraint) {
		return constraint.isImmediatelyCheckable();
	}

	@Override
	void simpleRemove(Generic generic) {
		if (!isAlive(generic))
			rollback(new AliveConstraintViolationException(generic + " is not alive"));
		if (generic.getClass().isAnnotationPresent(SystemGeneric.class) && generic.equals(find(generic.getClass())))
			rollback(new NotRemovableException("Cannot remove " + generic + " because it is System Generic annotated"));
		if (!(automatics.remove(generic) || adds.remove(generic)))
			removes.add(generic);
		super.simpleRemove(generic);
	}

	private void addGeneric(Generic generic, boolean automatic) throws ConstraintViolationException {
		if (automatic) {
			boolean result = automatics.add(generic);
			assert result;
		} else {
			boolean result = adds.add(generic);
			assert result;
			makeAncestorsManuel(generic);
		}
		simpleAdd(generic);
		check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	private void makeAncestorsManuel(Generic generic) {
		assert !isAutomatic(generic);
		recursiveMakeAncestorsManuel(generic, ((GenericImpl) generic).supers);
		recursiveMakeAncestorsManuel(generic, ((GenericImpl) generic).components);
	}

	private void recursiveMakeAncestorsManuel(Generic generic, Generic... ancestors) {
		for (Generic ancestor : ancestors)
			if (!ancestor.equals(generic)) {
				if (automatics.remove(ancestor)) {
					boolean result = adds.add(ancestor);
					assert result;
					makeAncestorsManuel(ancestor);
				}
			}
	}

	protected void removeGeneric(Generic generic) throws ConstraintViolationException {
		simpleRemove(generic);
		check(CheckingType.CHECK_ON_REMOVE_NODE, false, generic);
	}

	private void checkConstraints() throws ConstraintViolationException {
		check(CheckingType.CHECK_ON_ADD_NODE, adds);
		check(CheckingType.CHECK_ON_REMOVE_NODE, removes);
	}

	static class CacheDependencies implements TimestampedDependencies {

		private transient TimestampedDependencies underlyingDependencies;

		private final PseudoConcurrentSnapshot inserts = new PseudoConcurrentSnapshot();
		private final PseudoConcurrentSnapshot deletes = new PseudoConcurrentSnapshot();

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
			private final Iterator<Generic> underlyingIterator;
			private final Iterator<Generic> insertsIterator = inserts.iterator();

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

	@Override
	public int getLevel() {
		return subContext.getLevel() + 1;
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T reFind(Generic generic) {
		if (generic.isEngine() || generic.isAlive())
			return (T) generic;
		return new GenericBuilder(this, reFind(generic.getMeta()), ((GenericImpl) generic).getHomeTreeNode(), reFind(((GenericImpl) generic).supers), reFind(((GenericImpl) generic).selfToNullComponents()), Statics.MULTIDIRECTIONAL, false).find(false);
	}

	private Generic[] reFind(Generic... generics) {
		Generic[] reFounds = new Generic[generics.length];
		for (int i = 0; i < generics.length; i++)
			reFounds[i] = reFind(generics[i]);
		// TODO KK : if refind is null => exit caller method with null
		return reFounds;
	}

	public Generic findByDesignTs(Engine engine, ObjectInputStream in, Map<Long, Generic> genericMap) throws IOException {
		long ts = in.readLong();
		Generic superGeneric = genericMap.get(ts);
		return superGeneric != null ? superGeneric : ((GenericImpl) engine).getDesignTs() == ts ? engine : searchByDesignTs(ts);
	}

	@Override
	Generic searchByDesignTs(long ts) {
		for (Generic add : adds)
			if (((GenericImpl) add).getDesignTs() == ts)
				return add;
		for (Generic automatic : automatics)
			if (((GenericImpl) automatic).getDesignTs() == ts)
				return automatic;
		return subContext.searchByDesignTs(ts);
	}

	static class UnsafeCache extends CacheImpl {

		public UnsafeCache(Engine engine) {
			super(engine);
		}

		@Override
		protected boolean isImmediatelyCheckable(AbstractConstraintImpl constraint) {
			return false;
		}

		@Override
		protected boolean isImmediatelyConsistencyCheckable(AbstractConstraintImpl constraint) {
			return false;
		}

		@Override
		protected void check(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
			if (isFlushTime)
				super.check(checkingType, isFlushTime, generic);
		}
	}

}
