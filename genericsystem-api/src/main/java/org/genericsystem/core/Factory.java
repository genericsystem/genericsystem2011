package org.genericsystem.core;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.genericsystem.exception.CacheAwareException;

/**
 * Factory.
 * 
 * @author Nicolas Feybesse
 */
public interface Factory extends Serializable {

	/**
	 * Create a new Engine.
	 * 
	 * @param config
	 *            The Config.
	 * @param userClasses
	 *            List of user classes.
	 * @return The new Engine.
	 */
	Engine newEngine(Config config, Class<?>... userClasses);

	/**
	 * Create a new Generic.
	 * 
	 * @return The new Generic.
	 */
	Generic newGeneric(Class<?> clazz);

	/**
	 * Create a new Cache.
	 * 
	 * @param cache
	 *            The sub cache.
	 * @return The new Cache.
	 */
	Cache newCache(Cache subCache);

	/**
	 * Create a new Cache.
	 * 
	 * @param engine
	 *            The engine on which a cache is mount.
	 * @return The new Cache.
	 */
	Cache newCache(Engine engine);

	Cache getCacheLocal();

	/**
	 * Default Factory.
	 * 
	 * @author Nicolas Feybesse
	 */
	public static class DefaultFactory implements Factory {

		private static final long serialVersionUID = 374055825050083792L;

		private Class<Generic> genericClass;
		private Constructor<Engine> engineConstructor;
		private Constructor<Cache> cacheConstructorOnCache;
		private Constructor<Cache> cacheConstructorOnEngine;

		@SuppressWarnings({ "static-access", "unchecked" })
		public DefaultFactory(Class<?>... classes) {
			try {
				engineConstructor = this.<Engine> getImplementation((Class<Engine>) Class.forName("org.genericsystem.core.EngineImpl"), classes).getConstructor(Config.class, Class[].class);
				genericClass = this.<Generic> getImplementation((Class<Generic>) Class.forName("org.genericsystem.core.GenericImpl"), classes);
				cacheConstructorOnCache = this.<Cache> getImplementation((Class<Cache>) Class.forName("org.genericsystem.core.CacheImpl"), classes).getConstructor(Cache.class);
				cacheConstructorOnEngine = this.<Cache> getImplementation((Class<Cache>) Class.forName("org.genericsystem.core.CacheImpl"), classes).getConstructor(Engine.class);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		public DefaultFactory() {
			this(new Class<?>[0]);
		}

		@SuppressWarnings("unchecked")
		private static <T> Class<T> getImplementation(Class<T> interfaceClass, Class<?>[] classes) {
			for (Class<?> clazz : classes)
				if (interfaceClass.isAssignableFrom(clazz)) {
					if (!Generic.class.equals(interfaceClass))
						return (Class<T>) clazz;
					if (!Engine.class.isAssignableFrom(clazz))
						return (Class<T>) clazz;
				}
			return interfaceClass;
		}

		@Override
		public Engine newEngine(Config config, Class<?>... userClasses) {
			try {
				return engineConstructor.newInstance(config, userClasses);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public Generic newGeneric(Class<?> clazz) {
			try {
				return (Generic) (clazz != null && genericClass.isAssignableFrom(clazz) ? clazz.newInstance() : genericClass.newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public Cache newCache(Cache cache) {
			try {
				return cacheConstructorOnCache.newInstance(cache);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public Cache newCache(Engine engine) {
			try {
				return cacheConstructorOnEngine.newInstance(engine);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public Cache getCacheLocal() {
			throw new CacheAwareException("Unable to find the current cache. Have you forget to call start() method on current cache ?");
		}
	}

}
