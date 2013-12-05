package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.EngineImpl.GarbageCollectorManager;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GarbageTest extends AbstractTest {

	public void specializeGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		{
			Type vehicle = cache.setType("Vehicle");
			cache.flush();
			vehicle.remove();
			cache.flush();
		}
		GarbageCollectorManager garbageCollectorManager = ((EngineImpl) cache.getEngine()).getGarbageCollectorManager();
		garbageCollectorManager.runGarbage(0L);
		System.gc();
	}

	public void timeOutCache() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		try {
			Thread.sleep(100L);
			new RollbackCatcher() {

				@Override
				public void intercept() {
					((CacheImpl) cache).flush(0L);
				}
			}.assertIsCausedBy(ConcurrencyControlException.class);
		} catch (InterruptedException e) {
			assert false : e.getMessage();
		}
	}
}
