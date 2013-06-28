package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GetSubTypeTest extends AbstractTest {

	// Type

	public void testGetSubTypeSimpleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic subType = vehicle.getSubType(cache, "Car");
		assert Objects.equals(car, subType);
	}

	public void testGetSubTypeSimpleHierarchyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");

		assert cache.getEngine().getInstanceByValue(cache, "Car").equals(car.getImplicit());
		assert cache.getEngine().getInstances(cache).contains(car.getImplicit());
		assert cache.getEngine().getAllInstances(cache).contains(car.getImplicit()) : cache.getEngine().getAllInstances(cache);

		assert cache.getEngine().getSubType(cache, "Car").equals(car) : cache.getEngine().getSubType(cache, "Car");
		assert cache.getEngine().getSubTypes(cache).contains(car) : cache.getEngine().getSubTypes(cache);
	}

	public void testGetSubTypeDoubleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		vehicle.newSubType(cache, "Car");
		assert human.getSubType(cache, "Car") == null;
	}

	public void testGetSubTypeNonExistingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		assert vehicle.getSubType(cache, "Alien") == null;
	}

	public void testGetSubTypeDiamondInheritingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type plane = cache.newType("Plane");
		Type car = cache.newType("Car");
		cache.newSubType("FlyingCar", car, plane);
		Generic subTypeFromPlane = plane.getSubType(cache, "FlyingCar");
		Generic subTypeFromCar = car.getSubType(cache, "FlyingCar");
		assert Objects.equals(subTypeFromPlane, subTypeFromCar);
	}

	// Attribute

	public void testGetSubTypeSimpleHierarchyAttributeOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		power.setAttribute(cache, "Unit");
		assert power.getSubType(cache, "Unit") == null;
	}

	public void testGetSubTypeSimpleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		Attribute specializedPower = ((GenericImpl) car).setSubAttribute(cache, power, "SpecializedPower");
		Generic subAttribute = power.getSubType(cache, "SpecializedPower");
		assert Objects.equals(subAttribute, specializedPower);
	}

	public void testGetSubTypeDoubleHierarchyAttributeOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		power.setAttribute(cache, "Unit");
		Attribute wheel = car.setAttribute(cache, "Wheel");
		assert wheel.getSubType(cache, "Unit") == null;
	}

	public void testGetSubTypeDoubleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		((GenericImpl) car).setSubAttribute(cache, power, "SpecializedPower");
		Type human = cache.newType("Human");
		Attribute name = human.setAttribute(cache, "Name");
		assert name.getSubType(cache, "SpecializedPower") == null;
	}

	public void testGetSubTypeNonExistingAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.setAttribute(cache, "Power");
		assert vehicle.getSubType(cache, "Alien") == null;
	}

	public void testGetSubTypeDiamondInheritingAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		Attribute wheels = car.setAttribute(cache, "Wheels");
		cache.newSubType("WheelsPower", power, wheels);
		Generic subTypeFromPower = power.getSubType(cache, "WheelsPower");
		Generic subTypeFromWheels = wheels.getSubType(cache, "WheelsPower");
		assert Objects.equals(subTypeFromPower, subTypeFromWheels);
	}

	// Relation

	public void testGetSubTypeSimpleHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Type electricCar = car.newSubType(cache, "ElectricCar");
		Type subColor = color.newSubType(cache, "SubColor");
		Relation electricCarSubColor = ((GenericImpl) electricCar).setSubAttribute(cache, carColor, "ElectricCarSubColor", electricCar, subColor);
		Generic subRelation = carColor.getSubType(cache, "ElectricCarSubColor");
		assert Objects.equals(subRelation, electricCarSubColor);
	}

	public void testGetSubTypeDoubleHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Type electricCar = car.newSubType(cache, "ElectricCar");
		Type subColor = color.newSubType(cache, "SubColor");
		((GenericImpl) electricCar).setSubAttribute(cache, carColor, "ElectricCarSubColor", electricCar, subColor);
		Type plane = cache.newType("Plane");
		Type human = cache.newType("Human");
		Relation pilot = plane.setRelation(cache, "Pilot", human);
		assert pilot.getSubType(cache, "ElectricCarSubColor") == null;
	}

	public void testGetSubTypeNonExistingRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		car.setRelation(cache, "CarColor", color);
		assert car.getSubType(cache, "Pilot") == null;
	}

	public void testGetSubTypeDiamondInheritingRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Type plane = cache.newType("Plane");
		Type human = cache.newType("Human");
		Relation pilot = plane.setRelation(cache, "Pilot", human);
		cache.newSubType("CarColorPilot", carColor, pilot);
		Generic subTypeFromCarColor = carColor.getSubType(cache, "CarColorPilot");
		Generic subTypeFromPilot = pilot.getSubType(cache, "CarColorPilot");
		assert Objects.equals(subTypeFromCarColor, subTypeFromPilot);
	}
}
