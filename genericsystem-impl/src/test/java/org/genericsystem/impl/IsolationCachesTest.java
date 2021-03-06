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
		Cache cache = engine.newCache().start();
		// Adds a Vehicle Type in the cache and synchronizes it with engine.
		final Generic vehicle = cache.addType("Vehicle");
		cache.flush();
		// cache.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache().start();
		// After synchronization, Vehicle exists in cache n°2.
		assert cache2.isAlive(vehicle);
		// Removes the Vehicle Type in a cache n°2 without synchronization.
		vehicle.remove();
		// cache2.deactivate();

		// Creates a cache n°3 for engine.
		final Cache cache3 = engine.newCache();
		// Vehicle exists in cache n°3.
		assert cache3.isAlive(vehicle);
		// cache3.deactivate();

		// Cache n°2 synchronization attempt.
		// cache2.activate();
		cache2.start();
		assert !cache2.isAlive(vehicle);
		cache2.flush();
		// cache2.deactivate();

		// Vehicle still exists in cache n°3 because cache n°3 has read Vehicle before cache n°2 synchronization.
		// The cache n°2 synchronization will be visible in a later timestamp cache.
		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache3).getTs();
		// cache3.activate();

		cache3.start();
		assert cache3.isAlive(vehicle);
		// If you try to remove Vehicle from the cache n°3, you get an exception because Vehicle is already marked for removing.
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.remove();
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
		cache3.flush();
		// cache3.deactivate();
	}

	public void dirtyReadAddTest() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache n°1 for engine.
		Cache cache1 = engine.newCache().start();
		// Adds a Vehicle Type in the cache n°1 without synchronization.
		Generic vehicle = cache1.addType("Vehicle");
		// cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache().start();
		// Cache n°2 cannot read Vehicle Type.
		assert !cache2.isAlive(vehicle);
		// cache2.deactivate();

		// Cache n°1 synchronization attempt.
		// Result = Success. Vehicle Ancestry hasn't been read in a prior timestamp.
		// cache1.activate();
		cache1.start();
		cache1.flush();
		// cache1.deactivate();

		// Cache n°2 can read Vehicle Type as the previous synchronization succeeded.
		// cache2.activate();
		cache2.start();
		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache1).getTs();
		assert cache2.isAlive(vehicle);
		// cache2.deactivate();
	}

	public void dirtyReadAddTest2() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache().start();
		// Adds a Vehicle Type in the cache n°1 without synchronization.
		Generic vehicle = cache1.addType("Vehicle");
		// cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache().start();
		// Cache n°2 cannot read Vehicle Type.
		assert !cache2.isAlive(vehicle);
		// Reading of the ancestry (engine).
		assert cache2.isAlive(engine);
		// cache2.deactivate();

		// Cache n°1 synchronization attempt.
		// Result = Failed. Vehicle Ancestry has been read in a prior timestamp.
		// cache1.activate();
		cache1.start();
		cache1.flush();
		// cache1.deactivate();

		// Cache n°2 cannot read Vehicle Type as the previous synchronization failed.
		// cache2.activate();
		cache2.start();
		assert ((CacheImpl) cache2).getTs() < ((CacheImpl) cache1).getTs();
		assert !cache2.isAlive(vehicle);
		// cache2.deactivate();
	}

	public void phantomReadTest() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache().start();
		// Adds a Vehicle Type and two instances in the cache n°1 and synchronizes it with engine.
		Type vehicle = cache1.addType("Vehicle");
		vehicle.addInstance("Vehicle1");
		vehicle.addInstance("Vehicle2");
		cache1.flush();
		// cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache().start();
		assert (cache2.isAlive(vehicle));
		// Adds a new instance in the cache n°2 without synchronization.
		vehicle.addInstance("Vehicle3");
		// cache2.deactivate();

		// Creates a cache n°3 for engine.
		Cache cache3 = engine.newCache().start();
		// Iterates on the first instance.
		((Type) vehicle).getAllInstances().iterator().next();
		// cache3.deactivate();

		// cache2.activate();

		cache2.start();
		cache2.flush();
		assert (((Type) vehicle).getAllInstances().size() == 3);
		// cache2.deactivate();

		// cache3.activate();
		assert (cache3.isAlive(vehicle));
		assert (((Type) vehicle).getAllInstances().size() == 3);
		// cache3.deactivate();

		assert ((CacheImpl) cache2).getTs() < ((CacheImpl) cache3).getTs();
	}

	public void phantomReadTest2() {
		// Creates an engine.
		Engine engine = GenericSystem.newInMemoryEngine();

		// Create a first cache for engine.
		Cache cache1 = engine.newCache().start();
		// Adds a Vehicle Type and two instances in the cache n°1 and synchronizes it with engine.
		Type vehicle = cache1.addType("Vehicle");
		vehicle.addInstance("Vehicle1");
		vehicle.addInstance("Vehicle2");
		cache1.flush();
		// cache1.deactivate();

		// Creates a cache n°2 for engine.
		Cache cache2 = engine.newCache().start();
		assert (cache2.isAlive(vehicle));
		// Adds a new instance in the cache n°2 without synchronization.
		vehicle.addInstance("Vehicle3");
		// cache2.deactivate();

		// Creates a cache n°3 for engine.
		Cache cache3 = engine.newCache().start();
		assert (cache3.isAlive(vehicle));
		// Cache n°3 can read 2 instances.
		// The size() method iterates on all instances. Equivalent to cache3.isAlive(Vehicle).
		assert (((Type) vehicle).getAllInstances().size() == 2);
		// cache3.deactivate();

		// cache2.activate();

		cache2.start();
		cache2.flush();
		assert (((Type) vehicle).getAllInstances().size() == 3);
		// cache2.deactivate();

		// cache3.activate();
		// assert (cache3.isAlive(vehicle));
		assert (((Type) vehicle).getAllInstances().size() == 3);
		// cache3.deactivate();

		assert ((CacheImpl) cache2).getTs() > ((CacheImpl) cache3).getTs();
	}
}
