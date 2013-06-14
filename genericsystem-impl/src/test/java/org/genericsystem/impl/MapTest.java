package org.genericsystem.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MapTest extends AbstractTest {

	public void testPropertyMap() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Map<Serializable, Serializable> vehicleMap = vehicle.getProperties(cache);
		Map<Serializable, Serializable> map2 = new HashMap<>();
		Map<Serializable, Serializable> carMap = car.getProperties(cache);
		Map<Serializable, Serializable> myBmwMap = myBmw.getProperties(cache);

		for (int i = 0; i < 1000; i++) {
			int key = (int) (Math.random() * 10);
			assert Objects.equals(vehicleMap.get(key), map2.get(key));
			vehicleMap.put(key, i);
			map2.put(key, i);
			assert Objects.equals(vehicleMap.get(key), map2.get(key)) : "key : " + key + " i : " + i + " " + vehicleMap.get(key) + " " + map2.get(key);
			assert Objects.equals(map2.get(key), carMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
			assert Objects.equals(carMap.get(key), myBmwMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
		}

		vehicleMap.put("key2", false);
		assert !(Boolean) carMap.get("key2");
		carMap.put("key2", true);
		assert !(Boolean) vehicleMap.get("key2");
		assert (Boolean) carMap.get("key2");
		carMap.remove("key2");

		assert !(Boolean) vehicleMap.get("key2");
		assert !(Boolean) carMap.get("key2");
	}

	public void testPropertyInherit() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		vehicle.getProperties(cache).put("power", 123);
		assert vehicle.getProperties(cache).containsValue(123);
		myBmw.getProperties(cache).put("power", 255);
		myBmw.getProperties(cache).put("wheel", 4);
		assert !myBmw.getProperties(cache).containsValue(123) : myBmw.getProperties(cache);
		assert myBmw.getProperties(cache).containsValue(255);
		assert myBmw.getProperties(cache).containsValue(4);
		myBmw.getProperties(cache).remove("power");
		assert vehicle.getProperties(cache).containsValue(123);
		assert !myBmw.getProperties(cache).containsValue(255);
		assert myBmw.getProperties(cache).containsValue(123);
		assert myBmw.getProperties(cache).containsValue(4);
	}
}
