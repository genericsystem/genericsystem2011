package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class FlushTest {

	public void testFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		engine.close();

		cache = engine.newCache();
		assert engine.getInheritings().contains(human);
	}

	public void testNoFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type animal = cache.newType("Animal");
		Snapshot<Generic> snapshot = animal.getInheritings();
		assert snapshot.isEmpty();
		Type human = animal.newSubType("Human");
		assert snapshot.size() == 1;
		assert snapshot.contains(human);

		engine.close();

		cache = engine.newCache().start();
		assert engine.getInheritings().filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return Objects.equals(element.getValue(), "Animal");
			}
		}).isEmpty();
	}

	public void testPartialFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Type car = cache.newType("Car");
		engine.close();

		cache = engine.newCache().start();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;
		assert !snapshot.contains(car) : snapshot;
	}

	public void testMultipleCache() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;
		// cache.deactivate();

		cache = engine.newCache().start();
		Type car = cache.newType("Car");
		cache.flush();
		engine.close();

		cache = engine.newCache().start();
		snapshot = engine.getInheritings();
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;
	}

	public void testMultipleCache2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.newType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;

		cache = engine.newCache().start();
		Type car = cache.newType("Car");
		cache.flush();

		cache = engine.newCache();
		snapshot = engine.getInheritings();
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;

		engine.close();
	}

	public void testMultipleCache3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type vehicle = cache.newType("Vehicle");

		Cache cache2 = engine.newCache().start();
		assert cache2.getType("Vehicle") == null;

		cache.start().flush();

		cache2.start();
		((CacheImpl) cache2).pickNewTs();
		assert cache2.getType("Vehicle").equals(vehicle);
	}

	public void testAutomaticsNotFlushed() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		Generic red = color.newInstance("Red");
		Generic grey = color.newInstance("Grey");
		car.setLink(carColor, "DefaultCarColor", red);		// default color of car

	}

}
