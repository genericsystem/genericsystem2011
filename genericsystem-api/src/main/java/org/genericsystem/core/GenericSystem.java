package org.genericsystem.core;

import java.lang.reflect.InvocationTargetException;

import org.genericsystem.core.Factory.DefaultFactory;

/**
 * GenericSystem utilities for manage engines and caches.
 * 
 * @author Nicolas Feybesse
 */
public class GenericSystem {

	/**
	 * Create new an in memory Engine, create a new Cache on it.
	 * 
	 * @param userClasses
	 *            List of user classes.
	 * @return New activated Cache.
	 */
	public static Cache newCacheOnANewInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses).newCache();
	}

	/**
	 * Create new an in memory Engine, create a new Cache on it.
	 * 
	 * @param factory
	 *            The factory to Generic.
	 * @param userClasses
	 *            list of user classes
	 * @return new activated Cache
	 */
	public static Cache newCacheOnANewInMemoryEngine(Factory factory,
			Class<?>... userClasses) {
		return newInMemoryEngine(factory, userClasses).newCache();
	}

	/**
	 * Create a new persistent engine, create a new Cache on it.
	 * 
	 * @param userClasses
	 *            List of user classes.
	 * @return New activated Cache.
	 */
	public static Cache newCacheOnANewPersistentEngine(String directoryPath,
			Class<?>... userClasses) {
		return newCacheOnANewPersistentEngine(new DefaultFactory(),
				directoryPath, userClasses);
	}

	/**
	 * Create a new persistent Engine, create a new Cache on it.
	 * 
	 * @param factory
	 *            The factory to Generic.
	 * @param userClasses
	 *            List of user classes.
	 * @return New activated Cache.
	 */
	public static Cache newCacheOnANewPersistentEngine(Factory factory,
			String directoryPath, Class<?>... userClasses) {
		return newPersistentEngine(factory, directoryPath, userClasses)
				.newCache();
	}

	/**
	 * Creates a new in-memory Engine.
	 * 
	 * @param userClasses
	 *            List of user classes.
	 * 
	 * @return The new Engine.
	 */
	public static Engine newInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses);
	}

	/**
	 * Creates a new in-memory Engine.
	 * 
	 * @param factory
	 *            The factory to Generic.
	 * @param userClasses
	 *            List of user classes.
	 * 
	 * @return The new Engine.
	 */
	public static Engine newInMemoryEngine(Factory factory,
			Class<?>... userClasses) {
		return newPersistentEngine(factory, null, userClasses);
	}

	/**
	 * Creates a new persistent Engine.
	 * 
	 * @param directoryPath
	 *            Directory of persistence.
	 * @param userClasses
	 *            List of user classes.
	 * 
	 * @return The new Engine.
	 * @throws InvocationTargetException
	 */
	public static Engine newPersistentEngine(String directoryPath,
			Class<?>... userClasses) {
		return newPersistentEngine(new DefaultFactory(), directoryPath,
				userClasses);
	}

	/**
	 * Creates a new persistent Engine.
	 * 
	 * @param factory
	 *            The factory to Generic.
	 * @param directoryPath
	 *            Directory of persistence.
	 * @param userClasses
	 *            List of user classes.
	 * 
	 * @return The new Engine.
	 * @throws InvocationTargetException
	 */
	public static Engine newPersistentEngine(Factory factory,
			String directoryPath, Class<?>... userClasses) {
		try {
			return factory.newEngine(new Config(directoryPath, factory),
					userClasses);
		} catch (SecurityException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}
}
