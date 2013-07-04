package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testCacheOnCacheWithFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache cache2 = cache.newSuperCache().start();
		Type vehicle = cache2.newType("Vehicle");
		assert cache2.getEngine().getInheritings().contains(vehicle);
		cache.start();
		assert !cache.getEngine().getInheritings().contains(vehicle);
		cache2.start();
		cache2.flush();
		cache.start();
		assert cache.getEngine().getInheritings().contains(vehicle);
	}

}
