package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GetSubTypeTest extends AbstractTest {

	// Type

	public void testGetSubTypeSimpleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic subType = vehicle.getSubType(cache, "Car");
		assert Objects.equals(car, subType);
	}

	public void testGetSubTypeDoubleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic subType = human.getSubType(cache, "Car");
		assert !Objects.equals(car, subType);
	}

	public void testGetSubTypeNonExistingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		assert vehicle.getSubType(cache, "Alien") == null;
	}

	public void testGetSubTypeDiamondInheritingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type plane = cache.newType("Plane");
		Type car = cache.newType("Car");
		cache.newSubType("FlyingCar", car, plane);
		Generic subTypeFromPlane = plane.getSubType(cache, "FlyingCar");
		Generic subTypeFromCar = car.getSubType(cache, "FlyingCar");
		assert Objects.equals(subTypeFromPlane, subTypeFromCar);
	}

	// Attribute

	public void testGetSubTypeSimpleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		Attribute unit = power.setAttribute(cache, "Unit");
		log.info("test");
		power.log();
		unit.log();
		Generic subAttribute = power.getSubType(cache, "Unit");
		assert Objects.equals(unit, subAttribute); // KO
	}

	public void testGetSubTypeDoubleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setAttribute(cache, "power");
		Attribute carPowerUnit = carPower.setAttribute(cache, "Unit");
		Attribute wheel = car.setAttribute(cache, "Wheel");
		Generic subAttribute = wheel.getSubType(cache, "Unit");
		assert !Objects.equals(carPowerUnit, subAttribute);
	}

	// Relation

}
