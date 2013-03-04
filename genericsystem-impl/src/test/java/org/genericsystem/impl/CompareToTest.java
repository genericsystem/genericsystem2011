package org.genericsystem.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CompareToTest extends AbstractTest {

	public void testCompareTo() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic audi = car.newInstance(cache,"audi");
		assert car.compareTo(audi) < 0;
		assert audi.compareTo(car) > 0;
		assert car.compareTo(car) == 0;
	}

	public void testSort() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic audi = car.newInstance(cache,"audi");
		List<Generic> l = new ArrayList<>();
		l.add(audi);
		l.add(car);
		Collections.sort(l);
		assert l.get(0) == car;
		assert l.get(1) == audi;
	}
}
