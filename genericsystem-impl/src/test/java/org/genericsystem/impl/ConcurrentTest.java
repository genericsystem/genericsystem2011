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
		Cache cache = engine.newCache().start();
		Type car = cache.newType("Car");
		cache.mountNewCache().start();
		assert engine.getInheritings().contains(car);
		assert engine.getInheritings().contains(car);
	}

	public void testConcurrentFlush() {
		Engine engine = GenericSystem.newInMemoryEngine();
		Cache cache = engine.newCache().start();
		Generic car = cache.newType("Car");
		cache.flush();

		assert cache.isAlive(car);
		assert engine.getInheritings().contains(car);
		Cache cache2 = engine.newCache().start();
		assert cache2.isAlive(car);
		assert engine.getInheritings().contains(car);
	}

	public void testRemoveIntegrityConstraintViolation() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final Cache cache1 = engine.newCache().start();
		final Type car = cache1.newType("Car");
		Generic bmw = car.newInstance("bmw");
		cache1.flush();
		assert car.getInstances().contains(bmw);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveFlushConcurrent() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final CacheImpl cache1 = (CacheImpl) engine.newCache().start();
		final Generic car = cache1.newType("Car");
		cache1.flush();
		// cache1.deactivate();

		Cache cache2 = engine.newCache().start();

		cache1.start();
		car.remove();

		cache2.start();
		cache2.flush();
		// cache2.deactivate();

		// cache1.activate();

		cache1.start();
		cache1.pickNewTs();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove();
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
		// cache1.deactivate();
	}

	public void testRemoveFlushConcurrent2() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final Cache cache1 = engine.newCache().start();
		final Generic car = cache1.newType("Car");
		cache1.flush();

		Cache cache2 = engine.newCache().start();
		car.remove();
		cache2.flush();

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// Type car has already been removed by another thread
				cache1.start();
				car.remove();
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
	}

	public void testRemoveFlushConcurrent3() {
		Engine engine = GenericSystem.newInMemoryEngine();
		final CacheImpl cache1 = (CacheImpl) engine.newCache().start();
		final Generic car = cache1.newType("Car");
		cache1.flush();
		assert cache1.isAlive(car);
		// cache1.deactivate();
		CacheImpl cache2 = (CacheImpl) engine.newCache().start();
		assert cache2.getTs() > cache1.getTs();
		assert cache2.isAlive(car);
		car.remove();
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
				cache1.start();
				car.remove();
			}
		}.assertIsCausedBy(OptimisticLockConstraintViolationException.class);
		// cache1.deactivate();
	}

	public void testRemoveConcurrentMVCC() {
		Engine engine = GenericSystem.newInMemoryEngine();
		Cache cache1 = engine.newCache().start();
		Generic car = cache1.newType("Car");
		cache1.flush();
		// cache1.deactivate();

		CacheImpl cache2 = (CacheImpl) engine.newCache();
		assert cache2.isAlive(car);
		// cache2.deactivate();

		assert ((CacheImpl) cache1).getTs() < cache2.getTs();
		assert cache1.isAlive(car);

		// cache1.activate();
		car.remove();
		cache1.flush();
		assert !cache1.isAlive(car);
		assert ((CacheImpl) cache1).getTs() > cache2.getTs();
		// cache1.deactivate();
	}
}