package org.genericsystem.impl;

import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InterfacesTest extends AbstractTest {

	public void testInterfacesAndReverseInterfaces() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newType("Transformer", vehicle, robot);
		Type reverseTransformer = cache.newType("Transformer", robot, vehicle);

		assert transformer == reverseTransformer : transformer.info() + reverseTransformer.info();
	}

	public void testUnnecessary() {
		CacheImpl cache = (CacheImpl) GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Type electric = cache.newType("Electric");
		Type carElectric = cache.newType("CarElectric", car, vehicle, electric);
		assert !carElectric.getSupers().contains(vehicle);
	}

}
