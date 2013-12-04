package org.genericsystem.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.CacheImpl.UnsafeCache;
import org.genericsystem.core.Statics.AnonymousReference;
import org.genericsystem.core.Statics.TsGenerator;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.PropertiesMapProvider;
import org.genericsystem.map.SystemPropertiesMapProvider;
import org.genericsystem.systemproperties.MetaAttribute;
import org.genericsystem.systemproperties.MetaRelation;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@SystemGeneric
@StringValue(Statics.ROOT_NODE_VALUE)
public class EngineImpl extends GenericImpl implements Engine {

	private SystemCache systemCache = new SystemCache();
	private TsGenerator generator = new TsGenerator();
	private Factory factory;
	private Archiver archiver;

	private final GarbageCollectorManager garbageCollectorManager;

	public EngineImpl(Config config, Class<?>... userClasses) {
		factory = config.getFactory();
		archiver = new Archiver(this, config.getDirectoryPath());
		systemCache.init(userClasses);
		archiver.startScheduler();
		garbageCollectorManager = new GarbageCollectorManager();
		garbageCollectorManager.startScheduler();
	}

	void restoreEngine() {
		restore(new RootTreeNode(), pickNewTs(), pickNewTs(), 0L, Long.MAX_VALUE, new Generic[] { this }, Statics.EMPTY_GENERIC_ARRAY);
	}

	final void restoreEngine(long homeTreeNodeTs, long designTs, long birthTs, long lastReadTs, long deathTs) {
		assert homeTreeNodeTs != 0;
		restore(new RootTreeNode(homeTreeNodeTs), designTs, birthTs, lastReadTs, deathTs, new Generic[] { this }, Statics.EMPTY_GENERIC_ARRAY);
		assert components.length == 0;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	public GarbageCollectorManager getGarbageCollectorManager() {
		return garbageCollectorManager;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T buildComplex(HomeTreeNode homeTreeNode, Class<?> clazz, Generic[] supers, Generic[] components) {
		return (T) ((GenericImpl) getFactory().newGeneric(clazz)).initialize(homeTreeNode, supers, components);
	}

	@Override
	public Cache newCache() {
		return getFactory().newCache(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Attribute> T getMetaAttribute() {
		return (T) systemCache.get(MetaAttribute.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Relation> T getMetaRelation() {
		return (T) systemCache.get(MetaRelation.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T find(Class<?> clazz) {
		return (T) systemCache.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T findByDesignTs(final long designTs) {
		return unambigousFirst(new AbstractFilterIterator<T>(new AbstractPreTreeIterator<T>((T) this) {
			private static final long serialVersionUID = 2709522598573735797L;

			@Override
			public Iterator<T> children(T node) {
				return ((GenericImpl) node).dependenciesIterator();
			}
		}) {

			@Override
			public boolean isSelected() {
				return ((GenericImpl) next).getDesignTs() == designTs;
			}

		});
	}

	@Override
	public EngineImpl getEngine() {
		return this;
	}

	@Override
	public boolean isEngine() {
		return true;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	public AnonymousReference pickNewAnonymousReference() {
		return new AnonymousReference(pickNewTs());
	}

	@Override
	public boolean inheritsFrom(Generic generic) {
		return this.equals(generic);
	}

	@Override
	public int getMetaLevel() {
		return Statics.META;
	}

	private ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	public Cache start(Cache cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	public void stop(Cache cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public CacheImpl getCurrentCache() {
		Cache currentCache = cacheLocal.get();
		if (currentCache == null)
			currentCache = start(factory.getContextCache());
		return (CacheImpl) currentCache;
	}

	class RootTreeNode extends HomeTreeNode {
		RootTreeNode() {
			super(null, Statics.ROOT_NODE_VALUE);
		}

		RootTreeNode(long ts) {
			super(ts, null, Statics.ROOT_NODE_VALUE);
		}

		public long pickNewTs() {
			return EngineImpl.this.pickNewTs();
		}

		@Override
		public boolean isRoot() {
			return true;
		}

		@Override
		public RootTreeNode getHomeTree() {
			return this;
		}

		@Override
		public int getMetaLevel() {
			return Statics.META;
		}

		@Override
		public boolean inheritsFrom(HomeTreeNode homeTreeNode) {
			return equals(homeTreeNode);
		}

		@Override
		public String toString() {
			return "" + getValue();
		}

		@Override
		public HomeTreeNode findInstanceNode(Serializable value) {
			return Statics.ROOT_NODE_VALUE.equals(value) ? this : super.findInstanceNode(value);
		}

		@Override
		public HomeTreeNode bindInstanceNode(Serializable value) {
			return Statics.ROOT_NODE_VALUE.equals(value) ? this : super.bindInstanceNode(value);
		}
	}

	private class SystemCache extends HashMap<Class<?>, Generic> {

		private static final long serialVersionUID = 1150085123612887245L;

		private boolean startupTime = true;

		SystemCache init(Class<?>... userClasses) {
			List<Class<?>> classes = Arrays.<Class<?>> asList(EngineImpl.class, MetaAttribute.class, MetaRelation.class, NoInheritanceSystemType.class, SystemPropertiesMapProvider.class, PropertiesMapProvider.class, ConstraintsMapProvider.class);
			CacheImpl cache = (CacheImpl) start(new UnsafeCache(EngineImpl.this));
			for (Class<?> clazz : classes)
				get(clazz);
			for (Class<?> clazz : userClasses)
				get(clazz);
			cache.flush();
			stop(cache);
			startupTime = false;
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T extends Generic> T get(Class<?> clazz) {
			assert !Engine.class.equals(clazz);
			T systemProperty = (T) super.get(clazz);
			if (systemProperty != null) {
				assert systemProperty.isAlive();
				return systemProperty;
			}
			if (!startupTime)
				throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
			T result = getCurrentCache().<T> bind(clazz);
			put(clazz, result);
			((GenericImpl) result).mountConstraints(clazz);
			triggersDependencies(clazz);
			return result;
		}

		private void triggersDependencies(Class<?> clazz) {
			Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
			if (dependenciesClass != null)
				for (Class<?> dependencyClass : dependenciesClass.value())
					get(dependencyClass);
		}
	}

	public class GarbageCollectorManager extends LinkedHashSet<Generic> {

		private static final long serialVersionUID = -2021341943811568201L;
		private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		long ts = pickNewTs();
		long time = -1L;

		public void startScheduler() {
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					runGarbage(ts + Statics.LIFE_TIME_OUT);
				}
			}, Statics.GARBAGE_INITIAL_DELAY, Statics.GARBAGE_PERIOD, TimeUnit.MILLISECONDS);
		}

		public void runGarbage(long timeOut) {
			synchronized (getEngine()) {
				time = time == -1L ? ts : time + Statics.GARBAGE_PERIOD;
				Iterator<Generic> iterator = GarbageCollectorManager.this.iterator();
				while (iterator.hasNext()) {
					Generic generic = iterator.next();
					if (time >= timeOut) {
						((GenericImpl) generic).unplug();
						iterator.remove();
					}
				}
			}
		}
	}

	@Override
	public void close() {
		archiver.close();
	}

	@Override
	public String toString() {
		return Statics.ROOT_NODE_VALUE;
	}
}
