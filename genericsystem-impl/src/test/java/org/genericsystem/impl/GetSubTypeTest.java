package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.SuperRuleConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GetSubTypeTest extends AbstractTest {

	// Type

	public void testGetSubTypeSimpleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic subType = vehicle.getAllSubType("Car");
		assert Objects.equals(car, subType);
	}

	public void testGetSubTypeSimpleHierarchyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic myCar = car.addInstance("myCar");

		assert car.getInstance("myCar").equals(myCar);
		assert !vehicle.getInstances().contains(myCar) : vehicle.getInstances();
		assert vehicle.getAllInstances().contains(myCar) : vehicle.getAllInstances();

		assert cache.getEngine().getAllSubType("Car").equals(car) : cache.getEngine().getAllSubType("Car");
		assert cache.getEngine().getAllSubTypes().contains(car) : cache.getEngine().getAllSubTypes();
	}

	public void testGetSubTypeDoubleHierarchyType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		vehicle.addSubType("Car");
		assert human.getAllSubType("Car") == null;
	}

	public void testGetSubTypeNonExistingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		assert vehicle.getAllSubType("Alien") == null;
	}

	public void testGetSubTypeDiamondInheritingType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type plane = cache.addType("Plane");
		Type car = cache.addType("Car");
		car.addSubType("FlyingCar", new Generic[] { plane });
		Generic subTypeFromPlane = plane.getAllSubType("FlyingCar");
		Generic subTypeFromCar = car.getAllSubType("FlyingCar");
		assert Objects.equals(subTypeFromPlane, subTypeFromCar);
	}

	// Attribute

	public void testGetSubTypeSimpleHierarchyAttributeOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		power.setAttribute("Unit");
		assert power.getAllSubType("Unit") == null;
	}

	public void testGetSubTypeSimpleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		Attribute specializedPower = ((GenericImpl) car).setSubAttribute(power, "SpecializedPower");
		Generic subAttribute = power.getAllSubType("SpecializedPower");
		assert Objects.equals(subAttribute, specializedPower);
	}

	public void testGetSubTypeDoubleHierarchyAttributeOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		power.setAttribute("Unit");
		Attribute wheel = car.setAttribute("Wheel");
		assert wheel.getAllSubType("Unit") == null;
	}

	public void testGetSubTypeDoubleHierarchyAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		((GenericImpl) car).setSubAttribute(power, "SpecializedPower");
		Type human = cache.addType("Human");
		Attribute name = human.setAttribute("Name");
		assert name.getAllSubType("SpecializedPower") == null;
	}

	public void testGetSubTypeNonExistingAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.setAttribute("Power");
		assert vehicle.getAllSubType("Alien") == null;
	}

	public void testGetSubTypeDiamondInheritingAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		final Attribute power = car.setAttribute("Power");
		final Attribute wheels = car.setAttribute("Wheels");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				power.addSubType("WheelsPower", new Generic[] { wheels }).log();
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}

	// Relation

	public void testGetSubTypeSimpleHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Type electricCar = car.addSubType("ElectricCar");
		Type subColor = color.addSubType("SubColor");
		Relation electricCarSubColor = ((GenericImpl) electricCar).setSubAttribute(carColor, "ElectricCarSubColor", electricCar, subColor);
		Generic subRelation = carColor.getAllSubType("ElectricCarSubColor");
		assert Objects.equals(subRelation, electricCarSubColor);
	}

	public void testGetSubTypeDoubleHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Type electricCar = car.addSubType("ElectricCar");
		Type subColor = color.addSubType("SubColor");
		((GenericImpl) electricCar).setSubAttribute(carColor, "ElectricCarSubColor", subColor);
		Type plane = cache.addType("Plane");
		Type human = cache.addType("Human");
		Relation pilot = plane.setRelation("Pilot", human);
		assert pilot.getAllSubType("ElectricCarSubColor") == null;
	}

	public void testGetSubTypeDoubleHierarchyRelationWithOtherTargetType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		final Type car = vehicle.addSubType("Car");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				((GenericImpl) car).setSubAttribute(vehicleColor, "CarOutsideColor", cache.addType("Percent")).log();
			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void testGetSubTypeNonExistingRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		car.setRelation("CarColor", color);
		assert car.getAllSubType("Pilot") == null;
	}

	public void testGetSubTypeDiamondInheritingRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		final Relation carColor = car.setRelation("CarColor", color);
		Type plane = cache.addType("Plane");
		Type human = cache.addType("Human");
		final Relation pilot = plane.setRelation("Pilot", human);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				carColor.addSubType("CarColorPilot", new Generic[] { pilot });
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}
}
