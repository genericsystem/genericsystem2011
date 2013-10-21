package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl;
import org.genericsystem.constraints.AbstractConstraintImpl.CheckingType;
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
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractAwareIterator;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
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
			throw new NotRemovableException("Cannot remove " + generic + " because it is System Generic annotated");
		remove(generic);
		// Generic automatic = findAutomaticAlone(generic);
		// if (null != automatic)
		// remove(automatic);
	}

	private void remove(Generic generic) throws RollbackException {
		try {
			internalRemove(generic);
		} catch (ConstraintViolationException e) {
			rollback(e);
		}
	}

	// private Generic findAutomaticAlone(Generic generic) {
	// Generic automaticCandidate = generic.getImplicit();
	// if (automaticCandidate.isAlive() && automaticCandidate.isAutomatic() && automaticCandidate.getInheritings().isEmpty() && automaticCandidate.getComposites().isEmpty())
	// return automaticCandidate;
	// return null;
	// }

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
				// log.info("REBUILD : " + orderedDependency.info());
				Generic generic = buildAndInsertComplex(((GenericImpl) orderedDependency).getHomeTreeNode(), orderedDependency.getClass(),
						computeDirectSupers ? getDirectSupers(((GenericImpl) orderedDependency).primaries, adjust(((GenericImpl) orderedDependency).components)) : adjust(((GenericImpl) orderedDependency).supers),
						adjust(((GenericImpl) orderedDependency).components));
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
		Generic[] reBounds = new Generic[generics.length];
		for (int i = 0; i < generics.length; i++) {
			Generic generic = generics[i];
			reBounds[i] = generic == null ? null : generic.isAlive() ? generic : updateValue(generic, generic.getValue());
		}
		return reBounds;
	}

	@Override
	public void flush() throws RollbackException {
		assert equals(getEngine().getCurrentCache());
		Exception cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				checkConstraints();
				getSubContext().apply(adds, removes);

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
		T result = bind(this.<EngineImpl> getEngine().bindInstanceNode(value), userSupers, components, null, false);
		assert Objects.equals(value, result.getValue());
		return result;
	}

	@Override
	public <T extends Tree> T newTree(Serializable value) {
		return newTree(value, 1);
	}

	@Override
	public <T extends Tree> T newTree(Serializable value, int dim) {
		return this.<T> bind(this.<EngineImpl> getEngine().bindInstanceNode(value), new Generic[] { find(NoInheritanceSystemType.class) }, new Generic[dim], TreeImpl.class, false);
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

	<T extends Generic> T bind(Class<?> clazz) {
		Generic[] userSupers = findUserSupers(clazz);
		Generic[] components = findComponents(clazz);
		GenericImpl meta = getMeta(clazz);
		Serializable value = findImplictValue(clazz);
		HomeTreeNode homeTreeNode = meta.bindInstanceNode(value);
		T result = bind(homeTreeNode, Statics.insertFirst(meta, userSupers), components, clazz, false);
		return result;
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

	<T extends Generic> T bind(HomeTreeNode homeTreeNode, Class<?> specializationClass, Generic directSuper, boolean existsException, Generic... components) {
		components = ((GenericImpl) directSuper).sortAndCheck(components);
		return bind(homeTreeNode, new Generic[] { directSuper }, components, specializationClass, existsException);
	}

	private Class<?> getMetaInstanceGenericClass(Class<?> specializationClass, HomeTreeNode homeTreeNode, Generic[] supers) {
		Generic meta = getMeta(homeTreeNode, supers);
		InstanceGenericClass instanceClass = meta.getClass().getAnnotation(InstanceGenericClass.class);
		if (instanceClass != null)
			if (specializationClass == null || specializationClass.isAssignableFrom(instanceClass.value())) {
				specializationClass = instanceClass.value();
			} else {
				assert instanceClass.value().isAssignableFrom(specializationClass);
			}
		return specializationClass;
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> T getMeta(HomeTreeNode homeTreeNode, Generic[] supers) {
		HomeTreeNode metaNode = homeTreeNode.metaNode;
		GenericImpl generic = null;
		do {
			for (Generic superGeneric : generic == null ? supers : generic.supers) {
				if (((GenericImpl) superGeneric).homeTreeNode.inheritsFrom(metaNode)) {
					generic = (GenericImpl) superGeneric;
					break;
				}
			}
		} while (!generic.homeTreeNode.equals(metaNode));
		return (T) generic;
	}

	<T extends Generic> T bind(HomeTreeNode homeTreeNode, Generic[] supers, Generic[] components, Class<?> specializationClass, boolean existsException) {
		Primaries primarySet = new Primaries(homeTreeNode, supers);
		final HomeTreeNode[] primaries = primarySet.toArray();
		assert primaries.length != 0;
		return internalBind(homeTreeNode, primaries, components, specializationClass, existsException);
	}

	static long time1 = 0;
	static long time2 = 0;

	@SuppressWarnings("unchecked")
	<T extends Generic> T internalBind(HomeTreeNode homeTreeNode, HomeTreeNode[] primaries, Generic[] components, Class<?> specializationClass, boolean existsException) {
		Generic[] directSupers = getDirectSupers(primaries, components);

		for (Generic directSuper : directSupers)
			if (((GenericImpl) directSuper).equiv(primaries, components))
				if (directSupers.length == 1 && homeTreeNode.equals(((GenericImpl) directSuper).homeTreeNode))
					if (existsException)
						rollback(new ExistsException(directSuper + " already exists !"));
					else
						return (T) directSuper;
				else
					rollback(new FunctionalConsistencyViolationException(directSuper.info() + " " + Arrays.toString(directSupers)));

		if (homeTreeNode.getValue() != null) {
			T phantom = fastFindPhantom(homeTreeNode, primaries, components);
			if (phantom != null)
				phantom.remove();
		}
		long ts1 = System.currentTimeMillis();
		NavigableSet<Generic> orderedDependencies = getConcernedDependencies(primaries, components);
		long ts2 = System.currentTimeMillis();
		time1 += (ts2 - ts1);
		long ts3 = System.currentTimeMillis();
		NavigableSet<Generic> orderedDependencies2 = getConcernedDependencies2(directSupers, primaries, components);
		long ts4 = System.currentTimeMillis();
		time2 += (ts4 - ts3);
		// log.info("old vs new : " + time1 + " " + time2 + "  =========> " + (time1 - time2));
		// log.info("ZZZZZZZZ" + Arrays.toString(primaries));
		// log.info("ZZZZZZZZ" + orderedDependencies);
		// if (!orderedDependencies.isEmpty())
		// log.info("UUUUUUUUUUU" + orderedDependencies.first().info());
		// log.info("ZZZZZZZZ" + Arrays.toString(primaries));
		specializationClass = getMetaInstanceGenericClass(specializationClass, homeTreeNode, directSupers);

		assert orderedDependencies.equals(orderedDependencies2) : orderedDependencies + " " + orderedDependencies2;
		for (Generic generic : orderedDependencies.descendingSet())
			simpleRemove(generic);
		ConnectionMap connectionMap = new ConnectionMap();
		T superGeneric = buildAndInsertComplex(homeTreeNode, specializationClass, directSupers, components);
		connectionMap.reBind(orderedDependencies, true);
		return superGeneric;
	}

	NavigableSet<Generic> getConcernedDependencies(HomeTreeNode[] primaries, Generic[] components) {
		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		Iterator<Generic> removeIterator = concernedDependenciesIterator(getEngine(), primaries, components);
		while (removeIterator.hasNext()) {
			Generic next = removeIterator.next();
			orderedDependencies.add(next);
		}
		return orderedDependencies;
	}

	NavigableSet<Generic> getConcernedDependencies2(Generic[] supers, HomeTreeNode[] primaries, Generic[] components) {
		NavigableSet<Generic> orderedDependencies = new TreeSet<Generic>();
		for (Generic superGeneric : supers) {
			Iterator<Generic> removeIterator = concernedDependenciesIterator2(superGeneric, primaries, components);
			while (removeIterator.hasNext()) {
				Generic next = removeIterator.next();
				orderedDependencies.addAll(orderDependencies((GenericImpl) next));
			}
		}
		return orderedDependencies;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> concernedDependenciesIterator2(Generic directSuper, final HomeTreeNode[] primaries, final Generic[] components) {
		return new AbstractFilterIterator<T>(new AbstractPreTreeIterator<T>((T) directSuper) {

			private static final long serialVersionUID = 3038922934693070661L;

			@Override
			public Iterator<T> children(T node) {
				if (GenericImpl.isDependencyOf(primaries, components, ((GenericImpl) node).primaries, ((GenericImpl) node).components))
					return Collections.emptyIterator();
				else
					return new ConcateIterator<T>(((GenericImpl) node).<T> directInheritingsIterator(), ((GenericImpl) node).<T> compositesIterator());
			}
		}) {
			@Override
			public boolean isSelected() {
				return GenericImpl.isDependencyOf(primaries, components, ((GenericImpl) next).primaries, ((GenericImpl) next).components);
			}
		};
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> Iterator<T> concernedDependenciesIterator(final Generic directSuper, final HomeTreeNode[] primaries, final Generic[] components) {

		return new AbstractFilterIterator<T>((Iterator<T>) new AbstractPreTreeIterator<Generic>(directSuper) {

			private static final long serialVersionUID = 4540682035671625893L;

			@Override
			public Iterator<Generic> children(Generic node) {
				return new ConcateIterator<Generic>(((GenericImpl) node).directInheritingsIterator(), ((GenericImpl) node).compositesIterator());
			}
		}) {
			@Override
			public boolean isSelected() {
				return GenericImpl.isDependencyOf(primaries, components, ((GenericImpl) next).primaries, ((GenericImpl) next).components);
			}
		};
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

	protected void checkConsistency(boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		if (isConsistencyToCheck(isFlushTime, generic)) {
			AbstractConstraintImpl keyHolder = ((Holder) generic).getBaseComponent();
			keyHolder.check(((Holder) keyHolder.getBaseComponent()).getBaseComponent(), (Holder) generic, ((AxedPropertyClass) keyHolder.getValue()).getAxe());
		}
	}

	protected boolean isConsistencyToCheck(boolean isFlushTime, Generic generic) {
		if (!((GenericImpl) generic).isPhantomGeneric() && isConstraintValueSetting(generic))
			return isFlushTime || ((AbstractConstraintImpl) ((Holder) generic).getBaseComponent()).isImmediatelyConsistencyCheckable();
		return false;
	}

	public boolean isConstraintValueSetting(Generic generic) {
		return generic.isInstanceOf(find(ConstraintValue.class));
	}

	protected void check(CheckingType checkingType, boolean isFlushTime, Iterable<Generic> generics) throws ConstraintViolationException {
		for (Generic generic : generics)
			check(checkingType, isFlushTime, generic);
	}

	protected void check(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		checkConsistency(isFlushTime, generic);
		checkConstraints(checkingType, isFlushTime, generic);
	}

	private void checkConstraints(CheckingType checkingType, boolean isFlushTime, Generic generic) throws ConstraintViolationException {
		ExtendedMap<Serializable, Serializable> constraintMap = generic.getConstraintsMap();
		for (Serializable key : constraintMap.keySet()) {
			Holder valueHolder = constraintMap.getValueHolder(key);
			AbstractConstraintImpl keyHolder = valueHolder.getBaseComponent();
			if (isCheckable(keyHolder, generic, checkingType, isFlushTime))
				keyHolder.check(generic, valueHolder, ((AxedPropertyClass) keyHolder.getValue()).getAxe());
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
		check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	private void removeGeneric(Generic generic) throws ConstraintViolationException {
		simpleRemove(generic);
		check(CheckingType.CHECK_ON_REMOVE_NODE, false, generic);
	}

	private void checkConstraints() throws ConstraintViolationException {
		check(CheckingType.CHECK_ON_ADD_NODE, true, adds);
		check(CheckingType.CHECK_ON_REMOVE_NODE, true, removes);
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
		protected boolean isConsistencyToCheck(boolean isFlushTime, Generic generic) {
			if (!((GenericImpl) generic).isPhantomGeneric() && isConstraintValueSetting(generic))
				return isFlushTime;
			return false;
		}

		@Override
		<T extends Generic> T updateValue(final Generic old, final Serializable value) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					HomeTreeNode newHomeTreeNode = ((GenericImpl) old).getHomeTreeNode().metaNode.bindInstanceNode(value);
					return internalBind(newHomeTreeNode, new Primaries(Statics.insertFirst(newHomeTreeNode, Statics.truncate(((GenericImpl) old).primaries, ((GenericImpl) old).getHomeTreeNode()))).toArray(),
							reBind(((GenericImpl) old).selfToNullComponents()), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T addComponent(final Generic old, final Generic newComponent, final int pos) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.insertIntoArray(newComponent, ((GenericImpl) old).selfToNullComponents(), pos), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T removeComponent(final Generic old, final int pos) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), ((GenericImpl) old).supers, Statics.truncate(pos, ((GenericImpl) old).selfToNullComponents()), old.getClass(), false);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T addSuper(final Generic old, final Generic newSuper) {
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), Statics.insertLastIntoArray(newSuper, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), true);
				}
			}.rebuildAll(old);
		}

		@Override
		<T extends Generic> T removeSuper(final Generic old, final int pos) {
			if (pos == 0 && ((GenericImpl) old).supers.length == 1)
				throw new UnsupportedOperationException();
			return new Restructurator() {
				@Override
				Generic rebuild() {
					return bind(((GenericImpl) old).getHomeTreeNode(), Statics.truncate(pos, ((GenericImpl) old).supers), ((GenericImpl) old).selfToNullComponents(), old.getClass(), true);
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
