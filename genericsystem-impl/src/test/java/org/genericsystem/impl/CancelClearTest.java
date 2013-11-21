package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CancelClearTest extends AbstractTest {

	public void testCancelAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.newSubType("Car");
		assert car.getAttribute("power").equals(vehiclePower);
		car.cancel(vehiclePower);
		assert car.getAttribute("power") == null : car.getAttribute("power");
	}

	public void testCancelValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Type car = vehicle.newSubType("Car");
		assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
		car.cancel(vehicle233);
		assert car.getHolder(vehiclePower) == null : car.getHolder(vehiclePower);
	}

	public void testCancelValue2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Type car = vehicle.newSubType("Car");
		assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
		car.cancel(vehiclePower);
		assert car.getHolder(vehiclePower) == null : car.getHolder(vehiclePower);
	}

}