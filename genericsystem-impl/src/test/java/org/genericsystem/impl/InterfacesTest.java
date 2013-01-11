package org.genericsystem.impl;

import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.CacheImpl;
import org.testng.annotations.Test;

@Test
public class InterfacesTest extends AbstractTest {

	public void testInterfacesAndReverseInterfaces() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newSubType("Transformer", vehicle, robot);
		Type reverseTransformer = cache.newSubType("Transformer", robot, vehicle);

		assert transformer == reverseTransformer : transformer.info() + reverseTransformer.info();
	}

	public void testUnnecessary() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type electric = cache.newType("Electric");
		Type carElectric = cache.newSubType("CarElectric", car, vehicle, electric);
		assert !carElectric.getSupers().contains(vehicle);
	}

}
