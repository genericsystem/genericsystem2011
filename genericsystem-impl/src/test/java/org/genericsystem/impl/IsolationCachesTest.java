package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class IsolationCachesTest extends AbstractTest {

	public void dirtyReadRemovalTest() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for it.
		Cache cache = engine.newCache();
		// Adds a Vehicle Type in the cache and synchronizes it with engine.
		final Generic vehicle = cache.newType("Vehicle");
		cache.flush();
//		cache.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache();
		// After synchronization, Vehicle exists in cache n°2.
		assert cache2.isAlive(vehicle);
		// Removes the Vehicle Type in a cache n°2 without synchronization.
		vehicle.remove(cache2);
//		cache2.deactivate();

		// Creates a cache n°3 for engine.
		final Cache cache3 = engine.newCache();
		// Vehicle exists in cache n°3.
		assert cache3.isAlive(vehicle);
//		cache3.deactivate();

		// Cache n°2 synchronization attempt.
//		cache2.activate();
		assert !cache2.isAlive(vehicle);
		cache2.flush();
//		cache2.deactivate();

		// Vehicle still exists in cache n°3 because cache n°3 has read Vehicle before cache n°2 synchronization.
		// The cache n°2 synchronization will be visible in a later timestamp cache.
		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache3).getTs();
//		cache3.activate();
		assert cache3.isAlive(vehicle);
		// If you try to remove Vehicle from the cache n°3, you get an exception because Vehicle is already marked for removing.
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.remove(cache3);
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
		cache3.flush();
//		cache3.deactivate();
	}

	public void dirtyReadAddTest() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache n°1 for engine.
		Cache cache1 = engine.newCache();
		// Adds a Vehicle Type in the cache n°1 without synchronization.
		Generic vehicle = cache1.newType("Vehicle");
//		cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache();
		// Cache n°2 cannot read Vehicle Type.
		assert !cache2.isAlive(vehicle);
//		cache2.deactivate();

		// Cache n°1 synchronization attempt.
		// Result = Success. Vehicle Ancestry hasn't been read in a prior timestamp.
//		cache1.activate();
		cache1.flush();
//		cache1.deactivate();

		// Cache n°2 can read Vehicle Type as the previous synchronization succeeded.
//		cache2.activate();
		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache1).getTs();
		assert cache2.isAlive(vehicle);
//		cache2.deactivate();
	}

	public void dirtyReadAddTest2() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache();
		// Adds a Vehicle Type in the cache n°1 without synchronization.
		Generic vehicle = cache1.newType("Vehicle");
//		cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache();
		// Cache n°2 cannot read Vehicle Type.
		assert !cache2.isAlive(vehicle);
		// Reading of the ancestry (engine).
		assert cache2.isAlive(engine);
//		cache2.deactivate();

		// Cache n°1 synchronization attempt.
		// Result = Failed. Vehicle Ancestry has been read in a prior timestamp.
//		cache1.activate();
		cache1.flush();
//		cache1.deactivate();

		// Cache n°2 cannot read Vehicle Type as the previous synchronization failed.
//		cache2.activate();
		assert ((CacheImpl) cache2).getTs() < ((CacheImpl) cache1).getTs();
		assert !cache2.isAlive(vehicle);
//		cache2.deactivate();
	}

	public void phantomReadTest() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache();
		// Adds a Vehicle Type and two instances in the cache n°1 and synchronizes it with engine.
		Generic vehicle = cache1.newType("Vehicle");
		vehicle.newInstance(cache1,"Vehicle1");
		vehicle.newInstance(cache1,"Vehicle2");
		cache1.flush();
//		cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache();
		assert (cache2.isAlive(vehicle));
		// Adds a new instance in the cache n°2 without synchronization.
		vehicle.newInstance(cache2,"Vehicle3");
//		cache2.deactivate();

		// Creates a cache n°3 for engine.
		Cache cache3 = engine.newCache();
		// Iterates on the first instance.
		((Type) vehicle).getAllInstances(cache3).iterator().next();
//		cache3.deactivate();

//		cache2.activate();
		cache2.flush();
		assert (((Type) vehicle).getAllInstances(cache2).size() == 3);
//		cache2.deactivate();

//		cache3.activate();
		assert (cache3.isAlive(vehicle));
		assert (((Type) vehicle).getAllInstances(cache2).size() == 3);
//		cache3.deactivate();

		assert ((CacheImpl) cache2).getTs() < ((CacheImpl) cache3).getTs();
	}

	public void phantomReadTest2() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache();
		// Adds a Vehicle Type and two instances in the cache n°1 and synchronizes it with engine.
		Generic vehicle = cache1.newType("Vehicle");
		vehicle.newInstance(cache1,"Vehicle1");
		vehicle.newInstance(cache1,"Vehicle2");
		cache1.flush();
//		cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache();
		assert (cache2.isAlive(vehicle));
		// Adds a new instance in the cache n°2 without synchronization.
		vehicle.newInstance(cache2,"Vehicle3");
//		cache2.deactivate();

		// Creates a cache n°3 for engine.
		Cache cache3 = engine.newCache();
		assert (cache3.isAlive(vehicle));
		// Cache n°3 can read 2 instances.
		// The size() method iterates on all instances. Equivalent to cache3.isAlive(Vehicle).
		assert (((Type) vehicle).getAllInstances(cache3).size() == 2);
//		cache3.deactivate();

//		cache2.activate();
		cache2.flush();
		assert (((Type) vehicle).getAllInstances(cache2).size() == 3);
//		cache2.deactivate();

//		cache3.activate();
		assert (cache3.isAlive(vehicle));
		assert (((Type) vehicle).getAllInstances(cache2).size() == 3);
//		cache3.deactivate();

		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache3).getTs();
	}
}
