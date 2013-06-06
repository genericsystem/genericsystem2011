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
		Map<Serializable, Serializable> map = vehicle.getProperties(cache);
		Map<Serializable, Serializable> map2 = new HashMap<>();
		Map<Serializable, Serializable> map3 = car.getProperties(cache);
		Map<Serializable, Serializable> map4 = myBmw.getProperties(cache);

		for (int i = 0; i < 1000; i++) {
			int key = (int) (Math.random() * 10);
			assert Objects.equals(map.get(key), map2.get(key));
			map.put(key, i);
			map2.put(key, i);
			assert Objects.equals(map.get(key), map2.get(key)) : "key : " + key + " i : " + i + " " + map.get(key) + " " + map2.get(key);
			assert Objects.equals(map2.get(key), map3.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + map3.get(key);
			assert Objects.equals(map3.get(key), map4.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + map3.get(key);
		}

		map.put("key2", false);
		assert !(Boolean) map3.get("key2");
		map3.put("key2", true);
		assert !(Boolean) map.get("key2");
		assert (Boolean) map3.get("key2");
		map3.remove("key2");

		assert !(Boolean) map.get("key2");
		assert !(Boolean) map3.get("key2");
	}
}
