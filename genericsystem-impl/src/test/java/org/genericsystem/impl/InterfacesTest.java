package org.genericsystem.impl;

import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InterfacesTest extends AbstractTest {

	public void testInterfacesAndReverseInterfaces() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type robot = cache.addType("Robot");
		Type transformer = vehicle.setSubType("Transformer", new Generic[] { robot });
		Type reverseTransformer = robot.setSubType("Transformer", new Generic[] { vehicle });

		assert transformer == reverseTransformer : transformer.info() + reverseTransformer.info();
	}

	public void testUnnecessary() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type electric = cache.addType("Electric");
		Type carElectric = car.addSubType("CarElectric", new Generic[] { vehicle, electric });
		assert !carElectric.getSupers().contains(vehicle);
	}

}
