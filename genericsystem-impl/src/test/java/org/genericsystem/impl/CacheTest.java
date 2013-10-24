package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testCacheOnCacheWithFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache cache2 = cache.mountNewCache().start();
		Type vehicle = cache2.newType("Vehicle");
		assert cache2.getEngine().getInheritings().contains(vehicle);
		cache.start();
		assert !cache.getEngine().getInheritings().contains(vehicle);
		cache2.start();
		cache2.flush();
		cache.start();
		assert cache.getEngine().getInheritings().contains(vehicle);
	}

	public void testCacheLevel() {
		Cache mainCache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache superCache1 = mainCache.mountNewCache().start();
		Cache superCache2 = superCache1.mountNewCache().start();

		assert mainCache.getLevel() == 1;
		assert superCache1.getLevel() == 2;
		assert superCache2.getLevel() == 3;
	}

}
