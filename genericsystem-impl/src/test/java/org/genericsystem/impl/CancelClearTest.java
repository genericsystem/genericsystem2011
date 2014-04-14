package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class CancelClearTest extends AbstractTest {

	public void testCancelValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Type car = vehicle.addSubType("Car");
		assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
		car.cancel(vehicle233);
		assert car.getValue(vehiclePower) == null : car.getValue(vehiclePower);
	}

	public void testCancelValue2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Holder vehicle466 = vehicle.addValue(vehiclePower, 466);
		Type car = vehicle.addSubType("Car");
		assert car.getHolders(vehiclePower).contains(vehicle233) : car.getHolders(vehiclePower);
		assert car.getHolders(vehiclePower).contains(vehicle466) : car.getHolders(vehiclePower);
		car.cancel(vehicle233);
		assert !car.getHolders(vehiclePower).contains(vehicle233) : car.getHolders(vehiclePower);
		assert car.getHolders(vehiclePower).contains(vehicle466) : car.getHolders(vehiclePower);
	}

	public void testCancelAllValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic red = color.addInstance("red");
		Link carRed = car.bind(carColor, red);
		myCar.cancelAll(carColor, red);
		assert myCar.getLinks(carColor).isEmpty() : myCar.getLinks(carColor);
	}

	// public void testClearAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.addType("Vehicle");
	// Attribute vehiclePower = vehicle.addProperty("power");
	// Type car = vehicle.addSubType("Car");
	// assert car.getAttribute("power").equals(vehiclePower);
	// car.cancel(vehiclePower, Statics.STRUCTURAL);
	// car.clear(vehiclePower, Statics.STRUCTURAL);
	// assert car.getAttributes().contains(vehiclePower);
	// assert car.getAttribute("power") == vehiclePower;
	// }

	public void testClearValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Type car = vehicle.addSubType("Car");
		assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
		car.cancel(vehicle233);
		car.clear(vehicle233);
		assert car.getHolder(vehiclePower) == vehicle233 : car.getHolder(vehiclePower);
	}

	public void testClearValue2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
		Holder vehicle466 = vehicle.addValue(vehiclePower, 466);
		Type car = vehicle.addSubType("Car");
		assert car.getHolders(vehiclePower).contains(vehicle233) : car.getHolders(vehiclePower);
		assert car.getHolders(vehiclePower).contains(vehicle466) : car.getHolders(vehiclePower);
		car.cancel(vehicle233);
		car.clear(vehicle233);
		assert car.getHolders(vehiclePower).contains(vehicle233) : car.getHolders(vehiclePower);
		assert car.getHolders(vehiclePower).contains(vehicle466) : car.getHolders(vehiclePower);
	}

	// public void testClearValue2() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.addType("Vehicle");
	// Attribute vehiclePower = vehicle.addProperty("power");
	// Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
	// Type car = vehicle.addSubType("Car");
	// assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
	// car.cancel(vehiclePower, Statics.STRUCTURAL);
	// car.clear(vehiclePower, Statics.STRUCTURAL);
	// assert car.getValue(vehiclePower).equals(233);
	// assert car.getHolder(vehiclePower) == vehicle233 : car.getHolder(vehiclePower).info() + " / " + vehicle233.info();
	// }

	// public void testClearAllValue() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.addType("Vehicle");
	// Attribute vehiclePower = vehicle.addProperty("power");
	// Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
	// Type car = vehicle.addSubType("Car");
	// assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
	// car.cancelAll(vehicle233);
	// car.clearAll(vehicle233);
	// assert car.getHolder(vehiclePower) == vehicle233 : car.getHolder(vehiclePower);
	// }

	// public void testClearAllValue2() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.addType("Vehicle");
	// Attribute vehiclePower = vehicle.addProperty("power");
	// Holder vehicle233 = vehicle.addValue(vehiclePower, 233);
	// Type car = vehicle.addSubType("Car");
	// assert car.getHolder(vehiclePower).equals(vehicle233) : car.getHolder(vehiclePower);
	// car.cancelAll(vehiclePower, Statics.STRUCTURAL);
	// car.clearAll(vehiclePower, Statics.STRUCTURAL);
	// assert car.getHolder(vehiclePower) == vehicle233 : car.getHolder(vehiclePower);
	// }

}