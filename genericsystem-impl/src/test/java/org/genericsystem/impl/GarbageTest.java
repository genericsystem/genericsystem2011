package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.EngineImpl.GarbageCollectorManager;
import org.genericsystem.core.GenericSystem;
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
}
