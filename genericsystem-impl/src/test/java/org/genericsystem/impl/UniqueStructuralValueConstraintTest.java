package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.GetGenericConstraintVioliationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class UniqueStructuralValueConstraintTest extends AbstractTest {

	// Type

	public void testTwoTypesWithSameNameOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Type car2 = cache.setType("Car");
		assert Objects.equals(car, car2) : car.info() + car2.info();
	}

	public void testAttributeWithSameNameAsTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type power = cache.addType("Power");
		Attribute carPower = car.setAttribute("Power");
		assert carPower.getSupers().contains(power);
	}

	public void testRelationWithSameNameAsTypeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		final Type plane = cache.addType("Plane");
		Generic relation = car.setRelation("Plane", plane);
		assert car.getAttribute("Plane") == relation;
		assert cache.getEngine().getAllSubTypes("Plane").contains(plane);
	}

	// Attribute

	public void testAttributeWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute wheel = car.setAttribute("Wheels");
		Attribute wheel2 = car.setAttribute("Wheels");
		assert Objects.equals(wheel, wheel2);
	}

	public void testRelationWithSameNameAsAttributeOnSameGenericOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		final Type plane = cache.addType("Plane");
		Attribute wheelA = car.setAttribute("Wheels");
		Relation wheelR = car.setRelation("Wheels", plane);
		assert !Objects.equals(wheelA, wheelR);
	}

	public void testRelationWithSameNameAsAttributeOnDifferentGenericsOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type plane = cache.addType("Plane");
		Relation powerR = car.setRelation("Power", plane);
		Type color = cache.addType("Color");
		Attribute powerA = color.setAttribute("Power");
		assert !Objects.equals(powerA, powerR);
	}

	// Relation

	public void testTwoRelationsWithSameNameOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Relation carColor2 = car.setRelation("CarColor", color);
		assert Objects.equals(carColor, carColor2);
	}

	// 2 types

	public void testTwoTypesWithSameAttributeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Type plane = cache.addType("Plane");
		Attribute powerOnCar = car.setAttribute("Power");
		Attribute powerOnPlane = plane.setAttribute("Power");
		assert powerOnCar == car.getAttribute("Power");
		assert powerOnPlane == plane.getAttribute("Power");
		Type power = cache.addType("Power");
		assert power.fastValueEquals(powerOnCar);
		assert !powerOnCar.isAlive();
		assert !powerOnPlane.isAlive();
		assert ((GenericImpl) powerOnCar).reFind().inheritsFrom(power);
		assert ((GenericImpl) powerOnCar).reFind().inheritsFrom(power);
		assert ((GenericImpl) powerOnCar).reFind() == car.getAttribute("Power");
		assert ((GenericImpl) powerOnPlane).reFind() == plane.getAttribute("Power");
	}

	public void testTypeAndItsSubtypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		// assert ((GenericImpl) car).isStructuralNamingConstraintEnabled();
		assert car == car.setSubType("Car");
	}

	public void testOneTypeAndOneAttributeWithSameNameOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		car.setAttribute("Power");
		Generic power = cache.addType("Power");
		assert car.getAttribute("Power").getSupers().contains(power);
	}

	public void testTwoTypesWithSameRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type plane = cache.addType("Plane");
		Type color = cache.addType("Color");
		car.setRelation("ColorRelation", color);
		plane.setRelation("ColorRelation", color);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	// Instance

	public void testTwoInstancesWithSameNameOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myCar = car.setInstance("myCar");
		Generic myCar2 = car.setInstance("myCar");
		assert Objects.equals(myCar, myCar2);
	}

	public void testInstanceWithSameNameAsTypeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		Generic iCar = car.addInstance("Car");
		assert !Objects.equals(car, iCar);
	}

	// Holder

	public void testTwoHoldersWithSameValueOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		Generic myCar = car.addInstance("myCar");
		Holder myPower1 = myCar.setValue(power, 20000);
		Holder myPower2 = myCar.setValue(power, 20000);
		assert myPower1 == myPower2;
		assert Objects.equals(myPower1, myPower2);
	}

	public void testHolderWithSameValueAsTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setAttribute("Power");
		Generic myCar = car.addInstance("myCar");
		Holder myPower = myCar.setValue(power, "Car");
		assert !Objects.equals(myPower, car);
	}

	// Link

	public void testTwoLinksWithSameNameOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic myColor = color.addInstance("red");
		Link myVehicleRed1 = myCar.setLink(carColor, "myVehicleRed", myColor);
		Link myVehicleRed2 = myCar.setLink(carColor, "myVehicleRed", myColor);
		assert Objects.equals(myVehicleRed1, myVehicleRed2);
	}

	public void testLinkWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Attribute power = car.setAttribute("Power");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic myColor = color.addInstance("red");
		Link myVehicleRed = myCar.setLink(carColor, "Power", myColor);
		assert !Objects.equals(myVehicleRed, power);
	}

	public void testTwoTypesWithSameNameHeritingFromDifferentSupertypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type plant = cache.addType("Plant");
		final Type collection = cache.addType("Collection");
		plant.addSubType("Tree");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				collection.addSubType("Tree");
			}
		}.assertIsCausedBy(GetGenericConstraintVioliationException.class);
	}
}
