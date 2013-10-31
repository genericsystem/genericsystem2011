package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testFlushCache1OK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		cache.flush();
	}

	public void testFlushCacheOnCacheOK() {
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

	public void testCacheLevelOK() {
		Cache mainCache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache superCache1 = mainCache.mountNewCache().start();
		Cache superCache2 = superCache1.mountNewCache().start();

		assert mainCache.getLevel() == 1;
		assert superCache1.getLevel() == 2;
		assert superCache2.getLevel() == 3;
	}

	public void testFlushAndUnmountFirstCacheOk() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		assert Objects.equals(cache.flushAndUnmount(), cache);
	}

	public void testFlushAndUnmountSuperCacheOk() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache cache2 = cache.mountNewCache().start();
		assert Objects.equals(cache2.flushAndUnmount(), cache);
	}

	public void testDiscardAndUnmountFirstCacheOk() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		assert Objects.equals(cache.discardAndUnmount(), cache);
	}

	public void testDiscardAndUnmountSuperCacheOk() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Cache cache2 = cache.mountNewCache().start();
		assert Objects.equals(cache2.discardAndUnmount(), cache);
	}

}
