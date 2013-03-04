package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testCacheOnCacheWithFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Cache cache2 = cache.newSuperCache();
		Type vehicle = cache2.newType("Vehicle");
		assert cache2.getEngine().getInheritings(cache2).contains(vehicle);
		assert !cache.getEngine().getInheritings(cache).contains(vehicle);
		cache2.flush();
		assert cache.getEngine().getInheritings(cache).contains(vehicle);
	}

}
