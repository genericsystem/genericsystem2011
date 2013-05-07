package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConcurrentTest extends AbstractTest {

	public void testConcurrentWithNoFlush() {
		Engine engine = GenericSystem.newInMemoryEngine();
		Cache cache = engine.newCache();
		Type car = cache.newType("Car");
		assert engine.getInheritings(cache.newSuperCache()).contains(car);
		assert engine.getInheritings(cache).contains(car);
	}

	public void testConcurrentFlush() {
		Engine engine = GenericSystem.newInMemoryEngine();
		Cache cache = engine.newCache();
		Generic car = cache.newType("Car");
		cache.flush();

		assert cache.isAlive(car);
		assert engine.getInheritings(cache).contains(car);
		Cache cache2 = engine.newCache();
		assert cache2.isAlive(car);
		assert engine.getInheritings(cache2).contains(car);
	}

	public void testRemoveIntegrityConstraintViolation() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final Cache cache1 = engine.newCache();
		final Type car = cache1.newType("Car");
		Generic bmw = car.newInstance(cache1, "bmw");
		cache1.flush();
		assert car.getInstances(cache1).contains(bmw);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove(cache1);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveFlushConcurrent() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final CacheImpl cache1 = (CacheImpl) engine.newCache();
		final Generic car = cache1.newType("Car");
		cache1.flush();
		// cache1.deactivate();

		Cache cache2 = engine.newCache();
		car.remove(cache1);
		cache2.flush();
		// cache2.deactivate();

		// cache1.activate();
		cache1.pickNewTs();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove(cache1);
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
		// cache1.deactivate();
	}

	public void testRemoveFlushConcurrent2() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final Cache cache1 = engine.newCache();
		final Generic car = cache1.newType("Car");
		cache1.flush();

		Cache cache2 = engine.newCache();
		car.remove(cache2);
		cache2.flush();

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// Type car has already been removed by another thread
				car.remove(cache1);
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
	}

	public void testRemoveFlushConcurrent3() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final CacheImpl cache1 = (CacheImpl) engine.newCache();
		final Generic car = cache1.newType("Car");
		cache1.flush();
		assert cache1.isAlive(car);
		// cache1.deactivate();
		CacheImpl cache2 = (CacheImpl) engine.newCache();
		assert cache2.getTs() > cache1.getTs();
		assert cache2.isAlive(car);
		car.remove(cache2);
		assert !cache2.isAlive(car);
		cache2.flush();
		assert !cache2.isAlive(car);
		assert cache2.getTs() > cache1.getTs();
		// cache2.deactivate();

		// cache1.activate();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				// Type car has already been removed by another thread
				car.remove(cache1);
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
		// cache1.deactivate();
	}

	public void testRemoveConcurrentMVCC() {
		Engine engine = GenericSystem.newInMemoryEngine();
		Cache cache1 = engine.newCache();
		Generic car = cache1.newType("Car");
		cache1.flush();
		// cache1.deactivate();

		CacheImpl cache2 = (CacheImpl) engine.newCache();
		assert cache2.isAlive(car);
		// cache2.deactivate();

		assert ((CacheImpl) cache1).getTs() < cache2.getTs();
		assert cache1.isAlive(car);

		// cache1.activate();
		car.remove(cache1);
		cache1.flush();
		assert !cache1.isAlive(car);
		assert ((CacheImpl) cache1).getTs() > cache2.getTs();
		// cache1.deactivate();
	}
}