package org.genericsystem.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.genericsystem.annotation.Dependencies;
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
import org.genericsystem.iterator.AbstractFilterIterator;
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
	private static final long serialVersionUID = 6124326077696104707L;

	private AbstractContext subContext;

	private transient Map<Generic, TimestampedDependencies> compositeDependenciesMap;
	private transient Map<Generic, TimestampedDependencies> inheritingDependenciesMap;

	private Set<Generic> adds;
	private Set<Generic> removes;
	private Set<Generic> automatics;

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
		automatics = new HashSet<>();
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

	private <T extends Generic> NavigableSet<T> orderAndRemoveDependencies(final T old) {
		try {
			NavigableSet<T> orderedGenerics = orderDependencies(old);
			for (T generic : orderedGenerics.descendingSet())
				removeGeneric(generic);
			return orderedGenerics;
		} catch (ConstraintViolationException e) {
			rollback(e);
			return null;
		}
	}

	abstract class Restructurator {
		@SuppressWarnings("unchecked")
		<T extends Generic> T rebuildAll(Generic old) {
			NavigableSet<Generic> dependencies = orderAndRemoveDependencies(old);
			dependencies.remove(old);
			ConnectionMap map = new ConnectionMap();
			map.put(old, rebuild());
			return (T) map.reBind(dependencies).get(old);
		}

		abstract Generic rebuild();
	}

	void remove(final Generic generic, final RemoveStrategy removeStrategy) throws RollbackException {
		if (generic.getClass().isAnnotationPresent(SystemGeneric.class))
			rollback(new NotRemovableException("Cannot remove " + generic + " because it is System Generic annotated"));
		if (!isAlive(generic))
			rollback(new AliveConstraintViolationException(generic + " is not alive"));
		switch (removeStrategy) {
		case NORMAl:
			orderAndRemoveDependenciesForRemove(generic);
			break;
		case CONSERVE:
			// TODO faire marcher ça
			// new Restructurator() {
			// @Override
			// Generic rebuild() {
			// return null;
			// }
			// }.rebuildAll(generic);
			NavigableSet<Generic> dependencies = orderAndRemoveDependencies(generic);
			dependencies.remove(generic);
			for (Generic dependency : dependencies)
				bind(dependency.getMeta(), ((GenericImpl) dependency).getHomeTreeNode(), ((GenericImpl) generic).supers, ((GenericImpl) dependency).components, dependency.getClass(), Statics.MULTIDIRECTIONAL, false, true);
			break;
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
			@Override
			Generic rebuild() {
				Generic meta = old.getMeta();
				HomeTreeNode homeTreeNode = ((GenericImpl) meta).bindInstanceNode(value);
				return dependencyBind(meta, new Vertex(CacheImpl.this, homeTreeNode, ((GenericImpl) old).supers, ((GenericImpl) old).selfToNullComponents()), old.getClass(), Statics.MULTIDIRECTIONAL, false);
			}
		}.rebuildAll(old);
	}

	<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
		return new Restructurator() {
			@Override
			Generic rebuild() {
				return dependencyBind(old.getMeta(), new Vertex(CacheImpl.this, ((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos)), old.getClass(),
						Statics.MULTIDIRECTIONAL, false);
			}
		}.rebuildAll(old);
	}

	<T extends Generic> T removeComponent(final Generic old, final int pos) {
		return new Restructurator() {
			@Override
			Generic rebuild() {
				return dependencyBind(old.getMeta(), new Vertex(CacheImpl.this, ((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.truncate(pos, ((GenericImpl) old).selfToNullComponents())), old.getClass(),
						Statics.MULTIDIRECTIONAL, false);
			}
		}.rebuildAll(old);
	}

	<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
		return new Restructurator() {
			@Override
			Generic rebuild() {
				return dependencyBind(old.getMeta(), new Vertex(CacheImpl.this, ((GenericImpl) old).getHomeTreeNode(), Statics.insertLastIntoArray(newSuper, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents()), old.getClass(),
						Statics.MULTIDIRECTIONAL, true);
			}
		}.rebuildAll(old);
	}

	<T extends Generic> T removeSuper(final Generic old, final int pos) {
		if (pos == 0 && ((GenericImpl) old).supers.length == 1)
			rollback(new UnsupportedOperationException());
		return new Restructurator() {
			@Override
			Generic rebuild() {
				return dependencyBind(old.getMeta(), new Vertex(CacheImpl.this, ((GenericImpl) old).getHomeTreeNode(), Statics.truncate(pos, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents()), old.getClass(),
						Statics.MULTIDIRECTIONAL, true);
			}
		}.rebuildAll(old);
	}

	public <T extends Generic> T reBind(Generic generic) {
		return setValue(generic, generic.getValue());
	}

	@Override
	public boolean isAutomatic(Generic generic) {
		return automatics.contains(generic) || subContext.isAutomatic(generic);
	};

	//	public boolean isFlushable(Generic generic) {
	//		if (!isAutomatic(generic))
	//			return true;
	//		for (Generic inheriting : generic.getInheritings())
	//			if (isFlushable(inheriting))
	//				return true;
	//		for (Generic composite : generic.getComposites())
	//			if (isFlushable(composite))
	//				return true;
	//		return false;
	//	};

	public void markAsAutomatic(Generic generic) {
		automatics.add(generic);
	}

	public void markAsNonAutomatic(Generic generic) {
		automatics.remove(generic);
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
								return !((GenericImpl) next).isAutomatic();
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

				// if (attempt > Statics.ATTEMPTS / 2)
				// log.info("MvccException : " + e + " attempt : " + attempt);

				pickNewTs();
				continue;
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

	// @Override
	//	public <T extends Type> T newType(Serializable value) {
	//		return this.<T> newType(value);
	//	}

	@Override
	public <T extends Type> T getType(final Serializable value) {
		return getEngine().getSubType(value);
	}

	//	//@Override
	//	public <T extends Type> T addType(Serializable value, Type... userSupers) {
	//		return addType(value, userSupers, Statics.EMPTY_GENERIC_ARRAY);
	//	}
	//
	//	//@Override
	//	public <T extends Type> T addType(Serializable value, Type[] userSupers, Generic... components) {
	//		return bind(getEngine(), value, userSupers, components, null, Statics.MULTIDIRECTIONAL, false);
	//	}

	//	@Override
	//	public <T extends Type> T addType(Serializable name) {
	//		return this.<T> addType(name);
	//	}

	@Override
	public <T extends Type> T addType(Serializable name, Type... superTypes) {
		return addType(name, superTypes, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T addType(Serializable name, Type[] superTypes, Generic... components) {
		return bind(getEngine(), name, superTypes, components, null, Statics.MULTIDIRECTIONAL, false, true);
	}

	//	@Override
	//	public <T extends Type> T setType(Serializable name) {
	//		return this.<T> setType(name);
	//	}

	@Override
	public <T extends Type> T setType(Serializable name, Type... superTypes) {
		return setType(name, superTypes, Statics.EMPTY_GENERIC_ARRAY);
	}

	@Override
	public <T extends Type> T setType(Serializable name, Type[] superTypes, Generic... components) {
		return bind(getEngine(), name, superTypes, components, null, Statics.MULTIDIRECTIONAL, false, false);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value) {
		return newTree(value, 1);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value, int dim) {
		return this.<T> bind(getEngine(), value, new Generic[] { find(NoInheritanceSystemType.class) }, new Generic[dim], TreeImpl.class, Statics.MULTIDIRECTIONAL, false, false);
	}

	@Override
	public Cache mountNewCache() {
		return new CacheImpl(this).start();
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
		return this.<T> bind(meta, value, Statics.insertFirst(meta, userSupers), components, clazz, Statics.MULTIDIRECTIONAL, false, false);
	}

	<T extends Generic> T bind(Generic meta, Serializable value, Class<?> specializationClass, Generic directSuper, boolean existsException, int axe, Generic... components) {
		Generic[] sortAndCheck = ((GenericImpl) directSuper).sortAndCheck(components);
		return bind(meta, value, new Generic[] { directSuper }, sortAndCheck, specializationClass, Statics.MULTIDIRECTIONAL != axe ? findAxe(sortAndCheck, components[axe]) : axe, false, existsException);
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

	<T extends Generic> T bind(Generic meta, Serializable value, Generic[] supers, Generic[] components, Class<?> specializationClass, int basePos, boolean automatic, boolean existsException) {
		return bind(meta, ((GenericImpl) meta).bindInstanceNode(value), supers, components, specializationClass, basePos, automatic, existsException);
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T bind(Generic meta, HomeTreeNode homeTreeNode, Generic[] supers, Generic[] components, Class<?> specializationClass, int basePos, boolean automatic, boolean existsException) {
		GenericImpl generic = internalBind(meta, new Vertex(CacheImpl.this, homeTreeNode, supers, components), specializationClass, basePos, existsException);
		if (automatic)
			generic.markAsAutomatic();
		return (T) generic;
	}

	private class ConnectionMap extends HashMap<Generic, Generic> {
		private static final long serialVersionUID = 8257917150315417734L;

		private ConnectionMap reBind(Generic bind, Set<Generic> directDependencies, NavigableSet<Generic> allDependencies, int basePos) {
			for (Generic dependency : allDependencies)
				if (Statics.MULTIDIRECTIONAL == basePos || !((GenericImpl) bind).getComponent(basePos).equals(((Holder) dependency).getComponent(basePos)))
					if (directDependencies.contains(dependency))
						reBindDependency(bind, dependency, basePos);
					else
						reBindDependency(dependency);
				else
					// dependency is an update of bind
					put(dependency, bind);
			return this;
		}

		private void reBindDependency(Generic bind, Generic dependency, int basePos) {
			HomeTreeNode newHomeTreeNode = ((GenericImpl) dependency).getHomeTreeNode();
			Generic[] supers = adjust(((GenericImpl) dependency).supers);
			Generic[] components = adjust(((GenericImpl) dependency).selfToNullComponents());
			Generic meta = adjust(((GenericImpl) dependency).getMeta())[0];
			put(dependency, dependencyBind(meta, new Vertex(CacheImpl.this, newHomeTreeNode, supers, components), dependency.getClass(), basePos, false));
		}

		private ConnectionMap reBind(Set<Generic> orderedDependencies) {
			for (Generic dependency : orderedDependencies)
				reBindDependency(dependency);
			return this;
		}

		private void reBindDependency(Generic dependency) {
			Generic meta = adjust(((GenericImpl) dependency).getMeta())[0];
			HomeTreeNode homeTreeNode = ((GenericImpl) dependency).getHomeTreeNode();
			Generic[] supers = adjust(((GenericImpl) dependency).supers);
			Generic[] components = adjust(((GenericImpl) dependency).selfToNullComponents());
			put(dependency, dependencyBind(meta, new Vertex(CacheImpl.this, homeTreeNode, supers, components), dependency.getClass(), Statics.MULTIDIRECTIONAL, false));
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

	<T extends Generic> T dependencyBind(Generic meta, Vertex vertex, Class<?> specializationClass, int basePos, boolean existsException) throws RollbackException {
		boolean isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isSingularConstraintEnabled(basePos);
		boolean isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isPropertyConstraintEnabled();
		T result = vertex.muteAndFind(meta, isProperty, isSingular, basePos, existsException);
		if (result != null)
			return result;
		return buildAndInsertComplex(vertex.getHomeTreeNode(), ((GenericImpl) meta).specializeInstanceClass(specializationClass), vertex.getSupers(), vertex.getComponents());
	}

	<T extends Generic> T internalBind(Generic meta, Vertex vertex, Class<?> specializationClass, int basePos, boolean existsException) throws RollbackException {
		boolean isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isSingularConstraintEnabled(basePos);
		boolean isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isPropertyConstraintEnabled();
		T result = vertex.muteAndFind(meta, isProperty, isSingular, basePos, existsException);
		if (result != null)
			return result;
		Set<Generic> directDependencies = vertex.getDirectDependencies(meta, isProperty, isSingular, basePos);

		NavigableSet<Generic> allDependencies = new TreeSet<>();
		for (Generic dependency : directDependencies)
			allDependencies.addAll(orderDependencies(dependency));

		for (Generic dependency : allDependencies.descendingSet())
			simpleRemove(dependency);

		T bind = buildAndInsertComplex(vertex.getHomeTreeNode(), ((GenericImpl) meta).specializeInstanceClass(specializationClass), vertex.getSupers(), vertex.getComponents());

		new ConnectionMap().reBind(bind, directDependencies, allDependencies, basePos);
		return bind;
	}

	<T extends Generic> T buildAndInsertComplex(HomeTreeNode homeTreeNode, Class<?> clazz, Generic[] supers, Generic[] components) {
		return insert(this.<EngineImpl> getEngine().buildComplex(homeTreeNode, clazz, supers, components));
	}

	protected void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				find(dependencyClass);
	}

	protected void check(CheckingType checkingType, boolean isFlushTime, Iterable<Generic> generics) throws ConstraintViolationException {
		for (Generic generic : generics)
			check(checkingType, isFlushTime, generic);
	}

	protected void check(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		checkConsistency(checkingType, isFlushTime, generic);
		checkConstraints(checkingType, isFlushTime, generic);
	}

	private boolean isConsistencyToCheck(CheckingType checkingType, boolean isFlushTime, Generic generic) {
		if (isConstraintActivated(generic))
			if (isFlushTime || ((AbstractConstraintImpl) ((Holder) generic).getBaseComponent()).isImmediatelyConsistencyCheckable())
				return true;
		return false;
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

	private static class ConstraintComparator implements Comparator<AbstractConstraintImpl> {
		@Override
		public int compare(AbstractConstraintImpl o1, AbstractConstraintImpl o2) {
			if (o1.getPriority() == o2.getPriority())
				return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
			return Integer.compare(o1.getPriority(), o2.getPriority());
		}
	}

	private void checkConstraints(final CheckingType checkingType, final boolean isFlushTime, final Generic generic) throws ConstraintViolationException {
		for (final Attribute attribute : ((Type) generic).getAttributes()) {
			AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) attribute).getConstraintsMap();
			TreeMap<AbstractConstraintImpl, Holder> constraints = new TreeMap<>(new ConstraintComparator());
			for (AxedPropertyClass key : constraintMap.keySet()) {
				Holder valueHolder = constraintMap.getValueHolder(key);
				AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();
				constraints.put(keyHolder, valueHolder);
			}
			for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet()) {
				if (isCheckable(entry.getKey(), attribute, checkingType, isFlushTime)) {
					int axe = ((AxedPropertyClass) entry.getKey().getValue()).getAxe();
					if (AbstractAxedConstraintImpl.class.isAssignableFrom(entry.getKey().getClass()) && attribute.getComponent(axe) != null && (generic.isInstanceOf(attribute.getComponent(axe)))) {
						entry.getKey().check(attribute, generic, entry.getValue(), axe);
					}
				}
			}
		}
		AbstractExtendedMap<AxedPropertyClass, Serializable> constraintMap = ((GenericImpl) generic).getConstraintsMap();
		TreeMap<AbstractConstraintImpl, Holder> constraints = new TreeMap<>(new ConstraintComparator());
		for (AxedPropertyClass key : constraintMap.keySet()) {
			Holder valueHolder = constraintMap.getValueHolder(key);
			AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();
			constraints.put(keyHolder, valueHolder);
		}

		for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet()) {
			Holder valueHolder = entry.getValue();
			AbstractConstraintImpl keyHolder = entry.getKey();

			if (isCheckable(keyHolder, generic, checkingType, isFlushTime)) {
				Generic baseConstraint = ((Holder) keyHolder.getBaseComponent()).getBaseComponent();
				int axe = ((AxedPropertyClass) keyHolder.getValue()).getAxe();
				if (generic.getMetaLevel() - baseConstraint.getMetaLevel() >= 1)
					keyHolder.check(baseConstraint, AbstractAxedConstraintImpl.class.isAssignableFrom(keyHolder.getClass()) ? ((Attribute) generic).getComponent(axe) : generic, valueHolder, axe);
			}

		}
	}

	public boolean isCheckable(AbstractConstraintImpl constraint, Generic generic, CheckingType checkingType, boolean isFlushTime) {
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
		check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	protected void removeGeneric(Generic generic) throws ConstraintViolationException {
		simpleRemove(generic);
		check(CheckingType.CHECK_ON_REMOVE_NODE, false, generic);
	}

	private void checkConstraints() throws ConstraintViolationException {
		check(CheckingType.CHECK_ON_ADD_NODE, true, adds);
		check(CheckingType.CHECK_ON_REMOVE_NODE, true, removes);
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

}
