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

	public void testGetSubTypeSimpleHierarchyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");

		assert cache.getEngine().getInstanceByValue(cache, "Car").equals(car.getImplicit());
		assert cache.getEngine().getInstances(cache).contains(car.getImplicit());
		assert cache.getEngine().getAllInstances(cache).contains(car.getImplicit()) : cache.getEngine().getAllInstances(cache);

		assert cache.getEngine().getSubType(cache, "Car").equals(car) : cache.getEngine().getSubType(cache, "Car");
		assert cache.getEngine().getSubTypes(cache).contains(car) : cache.getEngine().getSubTypes(cache);
	}

	public void testGetSubTypeDoubleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		vehicle.newSubType(cache, "Car");
		assert human.getSubType(cache, "Car") == null;
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
		power.setAttribute(cache, "Unit");
		assert power.getSubType(cache, "Unit") == null;
	}

	public void testGetSubTypeDoubleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setAttribute(cache, "power");
		carPower.setAttribute(cache, "Unit");
		Attribute wheel = car.setAttribute(cache, "Wheel");
		assert wheel.getSubType(cache, "Unit") == null;
	}

	// Relation

}
