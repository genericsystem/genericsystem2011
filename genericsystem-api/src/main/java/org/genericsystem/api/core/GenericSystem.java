package org.genericsystem.api.core;

import java.lang.reflect.InvocationTargetException;

import org.genericsystem.api.core.Factory.DefaultFactory;

/**
 * GenericSystem utilities for manage engines and caches.
 * 
 * @author Nicolas Feybesse
 */
public class GenericSystem {

	/**
	 * Create new an in memory engine, create a new cache on it
	 * 
	 * @param userClasses
	 *            list of user classes
	 * @return new activated cache
	 */
	public static Cache newCacheOnANewInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses).newCache();
	}

	/**
	 * Create new an in memory engine, create a new cache on it
	 * 
	 * @param factory
	 *            the factory to generic
	 * @param userClasses
	 *            list of user classes
	 * @return new activated cache
	 */
	public static Cache newCacheOnANewInMemoryEngine(Factory factory, Class<?>... userClasses) {
		return newInMemoryEngine(factory, userClasses).newCache();
	}

	/**
	 * Create a new persistent engine, create a new cache on it
	 * 
	 * @param userClasses
	 *            list of user classes
	 * @return new activated cache
	 */
	public static Cache newCacheOnANewPersistentEngine(String directoryPath, Class<?>... userClasses) {
		return newCacheOnANewPersistentEngine(new DefaultFactory(), directoryPath, userClasses);
	}

	/**
	 * Create a new persistent engine, create a new cache on it
	 * 
	 * @param factory
	 *            the factory to generic
	 * @param userClasses
	 *            list of user classes
	 * @return new activated cache
	 */
	public static Cache newCacheOnANewPersistentEngine(Factory factory, String directoryPath, Class<?>... userClasses) {
		return newPersistentEngine(factory, directoryPath, userClasses).newCache();
	}

	/**
	 * Creates a new in-memory engine
	 * 
	 * @param userClasses
	 *            list of user classes
	 * 
	 * @return new engine
	 */
	public static Engine newInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses);
	}

	/**
	 * Creates a new in-memory engine
	 * 
	 * @param factory
	 *            the factory to generic
	 * @param userClasses
	 *            list of user classes
	 * 
	 * @return new engine
	 */
	public static Engine newInMemoryEngine(Factory factory, Class<?>... userClasses) {
		return newPersistentEngine(factory, null, userClasses);
	}

	/**
	 * Creates a new persistent engine
	 * 
	 * @param directoryPath
	 *            directory of persistence
	 * @param userClasses
	 *            list of user classes
	 * 
	 * @return new engine
	 * @throws InvocationTargetException
	 */
	public static Engine newPersistentEngine(String directoryPath, Class<?>... userClasses) {
		return newPersistentEngine(new DefaultFactory(), directoryPath, userClasses);
	}

	/**
	 * Creates a new persistent engine
	 * 
	 * @param factory
	 *            the factory to generic
	 * @param directoryPath
	 *            directory of persistence
	 * @param userClasses
	 *            list of user classes
	 * 
	 * @return new engine
	 * @throws InvocationTargetException
	 */
	public static Engine newPersistentEngine(Factory factory, String directoryPath, Class<?>... userClasses) {
		try {
			return factory.newEngine(new Config(directoryPath, factory), userClasses);
		} catch (SecurityException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}
}
