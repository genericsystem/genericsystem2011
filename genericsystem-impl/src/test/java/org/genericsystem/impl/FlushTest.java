package org.genericsystem.impl;

import java.util.Arrays;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class FlushTest {

	public void testFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		engine.close();

		cache = engine.newCache();
		assert engine.getInheritings(cache).contains(human);
	}

	public void testNoFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type animal = cache.newType("Animal");
		Snapshot<Generic> snapshot = animal.getInheritings(cache);
		assert snapshot.isEmpty();
		Type human = animal.newSubType(cache,"Human");
		assert snapshot.size() == 1;
		assert snapshot.contains(human);

		engine.close();

		cache = engine.newCache();
		assert engine.getInheritings(cache).findFirst("Animal") == null;
	}

	public void testPartialFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Type car = cache.newType("Car");
		engine.close();

		cache = engine.newCache();
		Snapshot<Generic> snapshot = engine.getInheritings(cache);
		assert snapshot.contains(human) : snapshot;
		assert !snapshot.contains(car) : snapshot;
	}

	public void testMultipleCache() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings(cache);
		assert snapshot.contains(human) : snapshot;
//		cache.deactivate();

		cache = engine.newCache();
		Type car = cache.newType("Car");
		cache.flush();
		engine.close();

		cache = engine.newCache();
		snapshot = engine.getInheritings(cache);
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;
	}

	public void testMultipleCache2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings(cache);
		assert snapshot.contains(human) : snapshot;

		cache = engine.newCache();
		Type car = cache.newType("Car");
		cache.flush();

		cache = engine.newCache();
		snapshot = engine.getInheritings(cache);
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;

		engine.close();
	}
}
