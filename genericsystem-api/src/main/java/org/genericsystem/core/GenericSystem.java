package org.genericsystem.core;

import java.lang.reflect.InvocationTargetException;

import org.genericsystem.core.Factory.DefaultFactory;

/**
 * <tt>Engine</tt> factory of Generic System. Assemble utilities for management of <tt>Engine</tt>
 * and <tt>Caches</tt>.
 */
public class GenericSystem {

	/**
	 * Creates a new In-Memory <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 * 
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses).newCache();
	}

	/**
	 * Creates a new In-Memory <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 * 
	 * @param factory
	 * 				the factory for generic.
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewInMemoryEngine(Factory factory,
			Class<?>... userClasses) {
		return newInMemoryEngine(factory, userClasses).newCache();
	}

	/**
	 * Creates a new Persistent <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 * 
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewPersistentEngine(String directoryPath,
			Class<?>... userClasses) {
		return newCacheOnANewPersistentEngine(new DefaultFactory(),
				directoryPath, userClasses);
	}

	/**
	 * Creates a new Persistent <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 * 
	 * @param factory
	 * 				the factory for generic.
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewPersistentEngine(Factory factory,
			String directoryPath, Class<?>... userClasses) {
		return newPersistentEngine(factory, directoryPath, userClasses)
				.newCache();
	}

	/**
	 * Creates and returns a new In-Memory <tt>Engine</tt>.
	 * 
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new engine.
	 */
	public static Engine newInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(new DefaultFactory(), userClasses);
	}

	/**
	 * Creates and returns a new In-Memory <tt>Engine</tt>.
	 * 
	 * @param factory
	 * 				the factory for generic.
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new engine.
	 */
	public static Engine newInMemoryEngine(Factory factory,
			Class<?>... userClasses) {
		return newPersistentEngine(factory, null, userClasses);
	}

	/**
	 * Creates and returns a new Persistent <tt>Engine</tt>.
	 * 
	 * @param directoryPath
	 * 				the directory of persistence.
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new engine.
	 * 
	 * @throws InvocationTargetException
	 */
	public static Engine newPersistentEngine(String directoryPath,
			Class<?>... userClasses) {
		return newPersistentEngine(new DefaultFactory(), directoryPath,
				userClasses);
	}

	/**
	 * Creates and returns a new Persistent <tt>Engine</tt>.
	 * 
	 * @param factory
	 * 				the factory for generic.
	 * @param directoryPath
	 * 				the directory of persistence.
	 * @param userClasses
	 * 				the list of user classes.
	 * 
	 * @return a new engine.
	 * 
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
