package org.genericsystem.impl;

import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.CacheImpl;
import org.testng.annotations.Test;

@Test
public class InterfacesTest extends AbstractTest {

	public void testInterfacesAndPrimaryInterfaces() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newSubType("Transformer", vehicle, robot);
		Generic myTransformer = transformer.newInstance(cache, "myTransformer");

		Generic bind = cache.add(transformer, "myTransformer", SystemGeneric.CONCRETE, new Generic[] { robot, vehicle }, new Generic[] {});
		assert myTransformer == bind : myTransformer.info() + bind.info();
	}

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
