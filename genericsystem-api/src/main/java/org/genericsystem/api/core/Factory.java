package org.genericsystem.api.core;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Nicolas Feybesse
 */
public interface Factory extends Serializable {

	Engine newEngine(Config config, Class<?>... userClasses);

	Generic newGeneric();

	Cache newCache(Context context);

	public static class DefaultFactory implements Factory {

		private static final long serialVersionUID = 374055825050083792L;

		private Class<Generic> genericClass;
		private Constructor<Engine> engineConstructor;
		private Constructor<Cache> cacheConstructor;

		@SuppressWarnings({ "static-access", "unchecked" })
		public DefaultFactory(Class<?>... classes) {
			try {
				engineConstructor = this.<Engine> getImplementation((Class<Engine>) Class.forName("org.genericsystem.impl.core.EngineImpl"), classes).getConstructor(Config.class, Class[].class);
				genericClass = this.<Generic> getImplementation((Class<Generic>) Class.forName("org.genericsystem.impl.core.GenericImpl"), classes);
				cacheConstructor = this.<Cache> getImplementation((Class<Cache>) Class.forName("org.genericsystem.impl.core.CacheImpl"), classes).getConstructor(Context.class);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException(e);
			}
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
		public Generic newGeneric() {
			try {
				return genericClass.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public Cache newCache(Context context) {
			try {
				return cacheConstructor.newInstance(context);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
