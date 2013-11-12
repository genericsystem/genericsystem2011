package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractAxedConstraintImpl;
import org.genericsystem.constraints.AbstractConstraintImpl.CheckingType;
import org.genericsystem.constraints.VirtualConstraintImpl;
import org.genericsystem.core.Generic.ExtendedMap;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.FunctionalConsistencyViolationException;
import org.genericsystem.exception.NotRemovableException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractAwareIterator;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.map.AxedPropertyClass;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintValue;
import org.genericsystem.snapshot.PseudoConcurrentSnapshot;
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

	void remove(final Generic generic) throws RollbackException {
		if (generic.getClass().isAnnotationPresent(SystemGeneric.class))
			rollback(new NotRemovableException("Cannot remove " + generic + " because it is System Generic annotated"));
		new UnsafeCacheManager<Object>() {
			@Override
			Object internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				unsafeCache.unsafeRemove(generic);
				return null;
			}
		}.doWork();

	}

	private abstract class UnsafeCacheManager<T> {
		private final UnsafeCache unsafeCache = new UnsafeCache(CacheImpl.this);

		T doWork() {
			try {
				unsafeCache.start();
				T result = internalWork(unsafeCache);
				unsafeCache.flush();
				return result;
			} catch (ConstraintViolationException ex) {
				rollback(ex);
				return null;
			} catch (RollbackException ex) {
				rollback(ex.getCause());
				return null;
			} finally {
				start();
			}
		}

		abstract T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException;
	}

	<T extends Generic> T updateValue(final Generic old, final Serializable value) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				return unsafeCache.unsafeUpdateValue(old, value);
			}
		}.doWork();
	}

	<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				return unsafeCache.unsafeAddComponent(old, newComponent, pos);
			}
		}.doWork();
	}

	<T extends Generic> T removeComponent(final Generic old, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				return unsafeCache.unsafeRemoveComponent(old, pos);
			}
		}.doWork();
	}

	<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				return unsafeCache.unsafeAddSuper(old, newSuper);
			}
		}.doWork();
	}

	<T extends Generic> T removeSuper(final Generic old, final int pos) {
		return new UnsafeCacheManager<T>() {
			@Override
			T internalWork(UnsafeCache unsafeCache) throws ConstraintViolationException {
				return unsafeCache.unsafeRemoveSuper(old, pos);
			}
		}.doWork();
	}

	private class ConnectionMap extends HashMap<Generic, Generic> {
		private static final long serialVersionUID = 8257917150315417734L;

		private ConnectionMap reBind(HomeTreeNode homeTreeNode, Generic bind, Set<Generic> orderedDependencies, boolean isProperty, boolean isSingular, int basePos, boolean rebuildSupers) {
			for (Generic orderedDependency : orderedDependencies) {
				HomeTreeNode newHomeTreeNode = null;
				HomeTreeNode[] primaries = null;
				Generic[] newComponents = null;
				if (isSingular || isProperty) {
					if (((GenericImpl) bind).getComponent(basePos).equals(((Holder) orderedDependency).getComponent(basePos)))
						continue;
					if (((GenericImpl) orderedDependency).components[basePos].inheritsFrom(((GenericImpl) bind).components[basePos])) {
						newHomeTreeNode = ((GenericImpl) orderedDependency).getHomeTreeNode();
						primaries = Statics.insertFirst(homeTreeNode, ((GenericImpl) orderedDependency).primaries);
					} else {
						newHomeTreeNode = homeTreeNode;
						primaries = Statics.replace(((GenericImpl) orderedDependency).primaries, ((GenericImpl) orderedDependency).getHomeTreeNode(), homeTreeNode);
					}
					Arrays.sort(primaries);
					newComponents = GenericImpl.enrich(adjust(((GenericImpl) orderedDependency).selfToNullComponents()), ((GenericImpl) bind).components);
				} else {
					newHomeTreeNode = ((GenericImpl) orderedDependency).getHomeTreeNode();
					primaries = ((GenericImpl) orderedDependency).primaries;
					newComponents = adjust(((GenericImpl) orderedDependency).components);
				}
				Generic build = buildAndInsertComplex(newHomeTreeNode, orderedDependency.getClass(), rebuildSupers ? getDirectSupers(primaries, newComponents) : adjust(((GenericImpl) orderedDependency).supers), newComponents);
				put(orderedDependency, build);
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

	@Override
	public boolean isAutomatic(Generic generic) {
		return automatics.contains(generic) || subContext.isAutomatic(generic);
	};

	public boolean isFlushable(Generic generic) {
		if (!isAutomatic(generic))
			return true;
		for (Generic inheriting : generic.getInheritings())
			if (isFlushable(inheriting))
				return true;
		for (Generic composite : generic.getComposites())
			if (isFlushable(composite))
				return true;
		return false;
	};

	public void markAsAutomatic(Generic generic) {
		automatics.add(generic);
	}

	@Override
	public void flush() throws RollbackException {
		assert equals(getEngine().getCurrentCache());
		Exception cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				checkConstraints();
				// adds.removeAll(automatics);
				getSubContext().apply(new Iterable<Generic>() {

					@Override
					public Iterator<Generic> iterator() {
						return new AbstractFilterIterator<Generic>(adds.iterator()) {

							@Override
							public boolean isSelected() {
								return ((GenericImpl) next).isFlushable();
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
		T result = bind(this.<EngineImpl> getEngine().bindInstanceNode(value), userSupers, components, null, false, Statics.MULTIDIRECTIONAL);
		assert Objects.equals(value, result.getValue());
		return result;
	}

	@Override
	public <T extends Tree> T newTree(Serializable value) {
		return newTree(value, 1);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value, int dim) {
		return this.<T> bind(this.<EngineImpl> getEngine().bindInstanceNode(value), new Generic[] { find(NoInheritanceSystemType.class) }, new Generic[dim], TreeImpl.class, false, Statics.MULTIDIRECTIONAL);
	}

	@Override
	public Cache mountNewCache() {
		return this.<EngineImpl> getEngine().getFactory().newCache(this);
	}

	@Override
	public Cache flushAndUnmount() {
		this.flush();
		AbstractContext subContext = this.getSubContext();
		if (subContext instanceof Cache)
			return (Cache) subContext;
		return this;
	}

	@Override
	public Cache discardAndUnmount() {
		this.clear();
		AbstractContext subContext = this.getSubContext();
		if (subContext instanceof Cache)
			return (Cache) subContext;
		return this;
	}

	<T extends Generic> T bind(Class<?> clazz) {
		Generic[] userSupers = findUserSupers(clazz);
		Generic[] components = findComponents(clazz);
		GenericImpl meta = getMeta(clazz);
		Serializable value = findImplictValue(clazz);
		HomeTreeNode homeTreeNode = meta.bindInstanceNode(value);
		assert homeTreeNode.getMetaLevel() <= 2;
		return this.<T> bind(homeTreeNode, Statics.insertFirst(meta, userSupers), components, clazz, false, Statics.MULTIDIRECTIONAL);
	}

	<T extends Generic> T bind(HomeTreeNode homeTreeNode, Class<?> specializationClass, Generic directSuper, boolean existsException, int axe, Generic... components) {
		Generic[] sortAndCheck = ((GenericImpl) directSuper).sortAndCheck(components);
		return bind(homeTreeNode, new Generic[] { directSuper }, sortAndCheck, specializationClass, existsException, Statics.MULTIDIRECTIONAL != axe ? findAxe(sortAndCheck, components[axe]) : axe);
	}

	int findAxe(Generic[] sorts, Generic baseComponent) {
		for (int i = 0; i < sorts.length; i++) {
			Generic sort = sorts[i];
			if (baseComponent.equals(sort))
				return i;
		}
		throw new IllegalStateException();
	}

	<T extends Generic> T bind(HomeTreeNode homeTreeNode, Generic[] supers, Generic[] components, Class<?> specializationClass, boolean existsException, int basePos) {
		// Generic[] oldSupers = supers;
		// Generic[] oldComponents = components;
		boolean isSingular = false;
		boolean isProperty = false;
		if (Statics.MULTIDIRECTIONAL != basePos) {
			Attribute attribute = (Attribute) supers[0];
			// GenericImpl baseComponent = (GenericImpl) components[basePos];
			isSingular = attribute.isSingularConstraintEnabled(basePos);
			isProperty = attribute.isPropertyConstraintEnabled();
			// Holder holder = null;
			// if (isSingular)
			// holder = baseComponent.getHolder(homeTreeNode.getMetaLevel(), attribute, basePos);
			// else if (isProperty)
			// holder = baseComponent.getHolder(homeTreeNode.getMetaLevel(), attribute, basePos, Statics.truncate(basePos, components));
			//
			// if (holder != null)
			// if (!components[basePos].equals(holder.getComponent(basePos)) || ((GenericImpl) holder).equiv(new Primaries(homeTreeNode, holder).toArray(), GenericImpl.enrich(components, ((GenericImpl) holder).components))) {
			// supers = new Generic[] { holder };
			// components = GenericImpl.enrich(components, ((GenericImpl) holder).components);
			// }
		}
		// HomeTreeNode[] oldPrimaries = new Primaries(homeTreeNode, oldSupers).toArray();
		// GenericImpl meta = this.<GenericImpl> getMeta(homeTreeNode, getDirectSupers(oldPrimaries, oldComponents));

		// Generic[] extendedDirectSupers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos, oldPrimaries, oldComponents);
		// supers = new Generic[] { extendedDirectSupers[0] };
		// components = GenericImpl.enrich(components, ((GenericImpl) extendedDirectSupers[0]).components);

		// HomeTreeNode[] primaries = new Primaries(homeTreeNode, supers).toArray();
		// Generic[] directSupers = getDirectSupers(primaries, components);

		// assert Arrays.equals(directSupers, extendedDirectSupers) : Arrays.toString(primaries) + "   " + Arrays.toString(components);

		// assert Arrays.equals(directSupers, extendedDirectSupers) : directSupers[0].info() + "   " + extendedDirectSupers[0].info();

		// assert Arrays.equals(directSupers, extendedDirectSupers) : Arrays.toString(directSupers) + "   " + Arrays.toString(extendedDirectSupers);

		return internalBind2(homeTreeNode, new Primaries(homeTreeNode, supers).toArray(), components, specializationClass, existsException, isSingular, isProperty, basePos);
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T internalBind2(HomeTreeNode homeTreeNode, HomeTreeNode[] primaries, Generic[] components, Class<?> specializationClass, boolean existsException, boolean isSingular, boolean isProperty, int basePos) {

		// Generic[] directSupers = getDirectSupers(primaries, components);

		GenericImpl meta = this.<GenericImpl> getMeta(homeTreeNode, getDirectSupers(primaries, components));

		Generic[] directSupers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos, primaries, components);
		for (Generic directSuper : directSupers) {
			primaries = new Primaries(directSuper, primaries).toArray();
			components = GenericImpl.enrich(components, ((GenericImpl) directSuper).components);
		}

		// Generic[] extendedDirectSupers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos, primaries, components);
		// assert Arrays.equals(directSupers, extendedDirectSupers);

		for (Generic directSuper : directSupers)
			if (((GenericImpl) directSuper).equiv(primaries, components))
				if (directSupers.length == 1 && homeTreeNode.equals(((GenericImpl) directSuper).homeTreeNode))
					if (existsException)
						rollback(new ExistsException(directSuper + " already exists !"));
					else
						return (T) directSuper;
				else
					rollback(new FunctionalConsistencyViolationException(directSuper.info() + " " + Arrays.toString(directSupers)));

		// if (!homeTreeNode.isPhantom()) {
		// T phantom = fastFindPhantom(homeTreeNode, primaries, components);
		// if (phantom != null)
		// phantom.remove();
		// }
		// NavigableSet<Generic> orderedDependencies = getConcernedDependencies(meta, isProperty, isSingular, primaries, components, basePos);
		NavigableSet<Generic> orderedDependencies = getConcernedDependencies2(new Generic[] { meta }, primaries, components, isProperty, isSingular, basePos);
		// log.info("" + orderedDependencies);
		// assert orderedDependencies.equals(concernedDependencies2) : orderedDependencies + " / " + concernedDependencies2 + " " + orderedDependencies.first().info();
		for (Generic generic : orderedDependencies.descendingSet())
			simpleRemove(generic);
		ConnectionMap connectionMap = new ConnectionMap();
		T bind = buildAndInsertComplex(homeTreeNode, specializeGenericClass(specializationClass, homeTreeNode, directSupers), directSupers, components);
		connectionMap.reBind(homeTreeNode, bind, orderedDependencies, isProperty, isSingular, basePos, true);
		return bind;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T internalBind(HomeTreeNode homeTreeNode, HomeTreeNode[] primaries, Generic[] components, Class<?> specializationClass, boolean existsException, boolean isSingular, boolean isProperty, int basePos) {
		Generic[] directSupers = getDirectSupers(primaries, components);
		GenericImpl meta = this.<GenericImpl> getMeta(homeTreeNode, directSupers);
		// Generic[] extendedDirectSupers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos, primaries, components);
		// assert Arrays.equals(directSupers, extendedDirectSupers);

		for (Generic directSuper : directSupers)
			if (((GenericImpl) directSuper).equiv(primaries, components))
				if (directSupers.length == 1 && homeTreeNode.equals(((GenericImpl) directSuper).homeTreeNode))
					if (existsException)
						rollback(new ExistsException(directSuper + " already exists !"));
					else
						return (T) directSuper;
				else
					rollback(new FunctionalConsistencyViolationException(directSuper.info() + " " + Arrays.toString(directSupers)));

		if (!homeTreeNode.isPhantom()) {
			T phantom = fastFindPhantom(homeTreeNode, primaries, components);
			if (phantom != null)
				phantom.remove();
		}
		// NavigableSet<Generic> orderedDependencies = getConcernedDependencies(meta, isProperty, isSingular, primaries, components, basePos);
		NavigableSet<Generic> orderedDependencies = getConcernedDependencies2(new Generic[] { meta }, primaries, components, isProperty, isSingular, basePos);
		// assert orderedDependencies.equals(concernedDependencies2) : orderedDependencies + " / " + concernedDependencies2 + " " + orderedDependencies.first().info();
		for (Generic generic : orderedDependencies.descendingSet())
			simpleRemove(generic);
		ConnectionMap connectionMap = new ConnectionMap();
		T bind = buildAndInsertComplex(homeTreeNode, specializeGenericClass(specializationClass, homeTreeNode, directSupers), directSupers, components);
		connectionMap.reBind(homeTreeNode, bind, orderedDependencies, isProperty, isSingular, basePos, true);
		return bind;
	}

	private Class<?> specializeGenericClass(Class<?> specializationClass, HomeTreeNode homeTreeNode, Generic[] supers) {
		Generic meta = getMeta(homeTreeNode, supers);
		InstanceGenericClass instanceClass = meta.getClass().getAnnotation(InstanceGenericClass.class);
		if (instanceClass != null)
			if (specializationClass == null || specializationClass.isAssignableFrom(instanceClass.value()))
				specializationClass = instanceClass.value();
			else
				assert instanceClass.value().isAssignableFrom(specializationClass);
		return specializationClass;
	}

	private GenericImpl getMeta(Class<?> clazz) {
		Extends extendsAnnotation = clazz.getAnnotation(Extends.class);
		if (null == extendsAnnotation)
			return getEngine();
		Class<?> meta = extendsAnnotation.meta();
		if (Engine.class.equals(meta))
			meta = EngineImpl.class;
		return this.<GenericImpl> find(meta);
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> T getMeta(HomeTreeNode homeTreeNode, Generic[] supers) {
		HomeTreeNode metaNode = homeTreeNode.metaNode;
		for (Generic superGeneric : supers)
			if (((GenericImpl) superGeneric).homeTreeNode.equals(metaNode))
				return (T) superGeneric;
		for (Generic superGeneric : supers)
			if (((GenericImpl) superGeneric).homeTreeNode.inheritsFrom(metaNode))
				return superGeneric.getMeta();
		throw new IllegalStateException();
	}

	private NavigableSet<Generic> getConcernedDependencies(Generic meta, boolean isProperty, boolean isSingular, HomeTreeNode[] primaries, Generic[] components, final int basePos) {
		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		Iterator<Generic> removeIterator = concernedDependenciesIterator(meta, isProperty, isSingular, getEngine(), primaries, components, basePos);
		while (removeIterator.hasNext()) {
			Generic next = removeIterator.next();
			orderedDependencies.add(next);
		}
		return orderedDependencies;
	}

	// TODO clean
	private NavigableSet<Generic> getConcernedDependencies2(Generic[] supers, HomeTreeNode[] primaries, Generic[] components, boolean isProperty, boolean isSingular, int basePos) {
		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		for (Generic superGeneric : supers) {
			Iterator<Generic> removeIterator = concernedDependenciesIterator2(superGeneric, primaries, components, isProperty, isSingular, basePos);
			while (removeIterator.hasNext()) {
				Generic next = removeIterator.next();
				orderedDependencies.addAll(orderDependencies((GenericImpl) next));
			}
		}
		return orderedDependencies;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> concernedDependenciesIterator2(final Generic meta, final HomeTreeNode[] primaries, final Generic[] components, final boolean isProperty, final boolean isSingular, final int basePos) {
		return new AbstractFilterIterator<T>(new AbstractPreTreeIterator<T>((T) meta) {

			private static final long serialVersionUID = 3038922934693070661L;

			{
				next();
			}

			@Override
			public Iterator<T> children(T node) {
				if (isAncestorOf(primaries, components, ((GenericImpl) node).primaries, ((GenericImpl) node).components))
					return Collections.emptyIterator();

				if (meta.getMetaLevel() != node.getMetaLevel()) {
					if (isSingular && basePos < ((GenericImpl) node).components.length && ((GenericImpl) node).components[basePos].inheritsFrom(components[basePos]))
						return Collections.emptyIterator();
					if (isProperty && Arrays.equals(((GenericImpl) node).components, components))
						return Collections.emptyIterator();
				}

				return new ConcateIterator<T>(((GenericImpl) node).<T> directInheritingsIterator(), ((GenericImpl) node).<T> compositesIterator());
			}
		}) {
			@Override
			public boolean isSelected() {
				if (isAncestorOf(primaries, components, ((GenericImpl) next).primaries, ((GenericImpl) next).components))
					return true;

				if (meta.getMetaLevel() != next.getMetaLevel()) {
					if (isSingular && basePos < ((GenericImpl) next).components.length && ((GenericImpl) next).components[basePos].inheritsFrom(components[basePos]))
						return true;
					if (isProperty && Arrays.equals(((GenericImpl) next).components, components))
						return true;
				}

				return false;
			}
		};
	}

	public static boolean isAncestorOf(HomeTreeNode[] primaries, Generic[] components, final HomeTreeNode[] subPrimaries, Generic[] subComponents) {
		if (GenericImpl.isSuperOf(primaries, components, subPrimaries, subComponents))
			return true;
		for (Generic component : subComponents)
			if (component != null)
				if (!Arrays.equals(subPrimaries, ((GenericImpl) component).primaries) || !Arrays.equals(subComponents, ((GenericImpl) component).components))
					if (isAncestorOf(primaries, components, ((GenericImpl) component).primaries, ((GenericImpl) component).components))
						return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> concernedDependenciesIterator(final Generic meta, final boolean isProperty, final boolean isSingular, final Generic directSuper, final HomeTreeNode[] primaries, final Generic[] components, final int basePos) {
		return new AbstractFilterIterator<T>((Iterator<T>) new AbstractPreTreeIterator<Generic>(meta) {
			private static final long serialVersionUID = 4540682035671625893L;
			{
				next();
			}

			@Override
			public Iterator<Generic> children(Generic node) {
				return new ConcateIterator<Generic>(((GenericImpl) node).directInheritingsIterator(), ((GenericImpl) node).compositesIterator());
			}
		}) {
			@Override
			public boolean isSelected() {
				if (isAncestorOf(primaries, components, ((GenericImpl) next).primaries, ((GenericImpl) next).components))
					return true;
				if (((GenericImpl) next).components.length != components.length)
					return false;
				if (isSingular && ((GenericImpl) next).components[basePos].inheritsFrom(components[basePos]))
					return true;
				if (isProperty)
					return Arrays.equals(((GenericImpl) next).components, components);
				return false;
			}
		};
	}

	<T extends Generic> T buildAndInsertComplex(HomeTreeNode homeTreeNode, Class<?> clazz, Generic[] supers, Generic[] components) {
		T bind = insert(this.<EngineImpl> getEngine().buildComplex(homeTreeNode, clazz, supers, components));
		if (bind.inheritsFrom(find(VirtualConstraintImpl.class)))
			assert bind.inheritsFrom(find(NoInheritanceSystemType.class));
		return bind;
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
			if (isFlushTime || isImmediatelyConsistencyCheckable((AbstractConstraintImpl) ((Holder) generic).getBaseComponent()))
				return true;
		return false;
	}

	protected boolean isConstraintActivated(Generic generic) {
		if (!((GenericImpl) generic).isPhantom())
			if (isConstraintValueSetting(generic))
				if (!Boolean.FALSE.equals(generic.getValue()))
					return true;
		return false;

	}

	protected boolean isConstraintValueSetting(Generic generic) {
		return generic.isInstanceOf(find(ConstraintValue.class));
	}

	protected boolean isImmediatelyConsistencyCheckable(AbstractConstraintImpl constraint) {
		return constraint.isImmediatelyConsistencyCheckable();
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
				if (null != component)
					for (Generic instance : component.getAllInstances())
						keyHolder.check(constraintBase, instance, (Holder) generic, axe);
			}
		}
	}

	private void checkConstraints(final CheckingType checkingType, final boolean isFlushTime, final Generic generic) throws ConstraintViolationException {
		class ConstraintComparator implements Comparator<AbstractConstraintImpl> {
			@Override
			public int compare(AbstractConstraintImpl o1, AbstractConstraintImpl o2) {
				if (o1.getPriority() < o2.getPriority())
					return -1;
				else if (o1.getPriority() > o2.getPriority())
					return 1;
				else
					return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
			}
		}

		for (final Attribute attribute : ((Type) generic).getAttributes()) {
			ExtendedMap<Serializable, Serializable> constraintMap = attribute.getConstraintsMap();
			TreeMap<AbstractConstraintImpl, Holder> constraints = new TreeMap<>(new ConstraintComparator());

			for (Serializable key : constraintMap.keySet()) {
				Holder valueHolder = constraintMap.getValueHolder(key);
				AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();

				constraints.put(keyHolder, valueHolder);
			}

			for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet()) {
				if (CacheImpl.this.isCheckable(entry.getKey(), attribute, checkingType, isFlushTime)) {
					int axe = ((AxedPropertyClass) entry.getKey().getValue()).getAxe();
					if (AbstractAxedConstraintImpl.class.isAssignableFrom(entry.getKey().getClass()) && attribute.getComponent(axe) != null && (generic.isInstanceOf(attribute.getComponent(axe)))) {
						entry.getKey().check(attribute, generic, entry.getValue(), axe);
					}
				}
			}

		}

		ExtendedMap<Serializable, Serializable> constraintMap = generic.getConstraintsMap();
		TreeMap<AbstractConstraintImpl, Holder> constraints = new TreeMap<>(new ConstraintComparator());
		for (Serializable key : constraintMap.keySet()) {
			Holder valueHolder = constraintMap.getValueHolder(key);
			AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();

			constraints.put(keyHolder, valueHolder);
		}

		for (Entry<AbstractConstraintImpl, Holder> entry : constraints.entrySet()) {
			Holder valueHolder = entry.getValue();
			AbstractConstraintImpl keyHolder = entry.getKey();

			if (CacheImpl.this.isCheckable(keyHolder, generic, checkingType, isFlushTime)) {
				Generic baseConstraint = ((Holder) keyHolder.getBaseComponent()).getBaseComponent();
				int axe = ((AxedPropertyClass) keyHolder.getValue()).getAxe();
				if (generic.getMetaLevel() - baseConstraint.getMetaLevel() >= 1)
					keyHolder.check(baseConstraint, AbstractAxedConstraintImpl.class.isAssignableFrom(keyHolder.getClass()) ? ((Attribute) generic).getComponent(axe) : generic, valueHolder, axe);
			}

		}
	}

	// private abstract class Check {
	// void check(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
	// ExtendedMap<Serializable, Serializable> constraintMap = generic.getConstraintsMap();
	// for (Serializable key : constraintMap.keySet()) {
	// Holder valueHolder = constraintMap.getValueHolder(key);
	// AbstractConstraintImpl keyHolder = valueHolder.<AbstractConstraintImpl> getBaseComponent();
	// if (CacheImpl.this.isCheckable(keyHolder, generic, checkingType, isFlushTime))
	// internalCheck(keyHolder, valueHolder, ((AxedPropertyClass) keyHolder.getValue()).getAxe());
	// }
	// }
	//
	// abstract void internalCheck(AbstractConstraintImpl keyHolder, Holder valueHolder, int axe) throws ConstraintViolationException;
	// }

	public boolean isCheckable(AbstractConstraintImpl constraint, Generic generic, CheckingType checkingType, boolean isFlushTime) {
		return (isFlushTime || isImmediatelyCheckable(constraint)) && constraint.isCheckedAt(generic, checkingType);
	}

	protected boolean isImmediatelyCheckable(AbstractConstraintImpl constraint) {
		return constraint.isImmediatelyCheckable();
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
		protected boolean isImmediatelyCheckable(AbstractConstraintImpl constraint) {
			return false;
		}

		@Override
		protected boolean isImmediatelyConsistencyCheckable(AbstractConstraintImpl constraint) {
			return false;
		}

		<T extends Generic> T unsafeUpdateValue(final Generic old, final Serializable value) throws ConstraintViolationException {
			assert value != null;
			return new Restructurator() {
				@Override
				Generic rebuild() {
					HomeTreeNode newHomeTreeNode = ((GenericImpl) old).getHomeTreeNode().metaNode.bindInstanceNode(value);
					// TODO call internalBind is forbidden, you must call bind
					return internalBind(newHomeTreeNode, new Primaries(Statics.insertFirst(newHomeTreeNode, Statics.truncate(((GenericImpl) old).primaries, ((GenericImpl) old).getHomeTreeNode()))).toArray(), ((GenericImpl) old).selfToNullComponents(),
							old.getClass(), false, false, false, Statics.MULTIDIRECTIONAL);
				}

			}.rebuildAll(old);
		}

		<T extends Generic> T unsafeAddComponent(final Generic old, final Generic newComponent, final int pos) throws ConstraintViolationException {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos), old.getClass(), false, Statics.MULTIDIRECTIONAL);
				}
			}.rebuildAll(old);
		}

		<T extends Generic> T unsafeRemoveComponent(final Generic old, final int pos) throws ConstraintViolationException {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.truncate(pos, ((GenericImpl) old).selfToNullComponents()), old.getClass(), false, Statics.MULTIDIRECTIONAL);
				}
			}.rebuildAll(old);
		}

		<T extends Generic> T unsafeAddSuper(final Generic old, final Generic newSuper) throws ConstraintViolationException {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), Statics.insertLastIntoArray(newSuper, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), true, Statics.MULTIDIRECTIONAL);
				}
			}.rebuildAll(old);
		}

		void unsafeRemove(Generic generic) throws ConstraintViolationException {
			if (!isAlive(generic))
				throw new AliveConstraintViolationException(generic + " is not alive");
			orderAndRemoveDependenciesForRemove(generic);
		}

		<T extends Generic> T unsafeRemoveSuper(final Generic old, final int pos) throws ConstraintViolationException {
			if (pos == 0 && ((GenericImpl) old).supers.length == 1)
				throw new UnsupportedOperationException();
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), Statics.truncate(pos, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), true, Statics.MULTIDIRECTIONAL);
				}
			}.rebuildAll(old);
		}

		private <T extends Generic> NavigableSet<T> orderAndRemoveDependencies(final T old) throws ConstraintViolationException {
			NavigableSet<T> orderedGenerics = orderDependencies(old);
			for (T generic : orderedGenerics.descendingSet())
				removeGeneric(generic);
			return orderedGenerics;
		}

		private <T extends Generic> NavigableSet<T> orderAndRemoveDependenciesForRemove(final T old) throws ConstraintViolationException {
			NavigableSet<T> orderedGenerics = orderDependenciesForRemove(old);
			for (T generic : orderedGenerics.descendingSet())
				removeGeneric(generic);
			return orderedGenerics;
		}

		abstract class Restructurator {
			@SuppressWarnings("unchecked")
			<T extends Generic> T rebuildAll(Generic old) throws ConstraintViolationException {
				NavigableSet<Generic> dependencies = orderAndRemoveDependencies(old);
				dependencies.remove(old);
				ConnectionMap map = new ConnectionMap();
				GenericImpl rebuild = (GenericImpl) rebuild();
				map.put(old, rebuild);
				return (T) map.reBind(rebuild.getHomeTreeNode(), rebuild, dependencies, false, false, Statics.MULTIDIRECTIONAL, false).get(old);
			}

			abstract Generic rebuild();
		}

	}

	@Override
	public int getLevel() {
		if (subContext != null && subContext instanceof Cache)
			return 1 + ((Cache) subContext).getLevel();
		return 1;
	}
}
