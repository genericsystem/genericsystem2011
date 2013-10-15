package org.genericsystem.impl;

import java.util.Objects;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
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

	public void testTypeWithSameNameAsTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type car2 = cache.newType("Car");
		assert Objects.equals(car, car2);
	}

	public void testAttributeWithSameNameAsTypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.newType("Car");
		cache.newType("Plane");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.setAttribute("Plane");
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	public void testRelationWithSameNameAsTypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.setRelation("Plane", plane);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	// Attribute

	public void testAttributeWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute wheel = car.setAttribute("Wheels");
		Attribute wheel2 = car.setAttribute("Wheels");
		assert Objects.equals(wheel, wheel2);
	}

	public void testRelationWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type plane = cache.newType("Plane");
		Attribute wheelAttribute = car.setAttribute("Wheels");
		Relation wheelRelation = car.setRelation("Wheels", plane);
		assert !Objects.equals(wheelAttribute, wheelRelation);
	}

	// Relation

	public void testRelationWithSameNameAsRelationOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Relation carColor2 = car.setRelation("CarColor", color);
		assert Objects.equals(carColor, carColor2);
	}

	// 2 types

	public void testTwoTypesWithSameAttributeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");
		Attribute powerOnCar = car.setAttribute("Power");
		Attribute powerOnPlane = plane.setAttribute("Power");

		assert powerOnCar == car.getAttribute("Power");
		assert powerOnPlane == plane.getAttribute("Power");
		Statics.debugCurrentThread();
		Type power = cache.newType("Power");
		assert power.fastValueEquals(powerOnCar);
		assert !powerOnCar.isAlive();
		assert !powerOnPlane.isAlive();
		assert powerOnCar.inheritsFrom(power);
		assert powerOnPlane.inheritsFrom(power);

		assert ((GenericImpl) powerOnCar).reFind() == car.getAttribute("Power");
		assert ((GenericImpl) powerOnPlane).reFind() == plane.getAttribute("Power");
	}

	public void testTwoTypesWithSameRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");
		final Type color = cache.newType("Color");
		car.setRelation("ColorRelation", color);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				plane.setRelation("ColorRelation", color);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	// Instance

	public void testInstanceWithSameNameAsInstanceOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance("myCar");
		Generic myCar2 = car.newInstance("myCar");
		assert Objects.equals(myCar, myCar2);
	}

	public void testInstanceWithSameNameAsTypeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.newType("Car");
		Generic myCar = car.newInstance("Car");
		assert !Objects.equals(car, myCar);
	}

	// Holder

	public void testHolderWithSameValueAsHolderOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute("Power");
		Generic myCar = car.newInstance("myCar");
		Holder myPower1 = myCar.setValue(power, 20000);
		Holder myPower2 = myCar.setValue(power, 20000);
		assert Objects.equals(myPower1, myPower2);
	}

	public void testHolderWithSameValueAsTypeNameKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute("Power");
		Generic myCar = car.newInstance("myCar");
		Holder myPower = myCar.setValue(power, "Car");
		assert !Objects.equals(myPower, car);
	}

	// Link

	public void testLinkWithSameNameAsLinkOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myCar = car.newInstance("myCar");
		Generic myColor = color.newInstance("red");
		Link myVehicleRed1 = myCar.setLink(carColor, "myVehicleRed", myColor);
		Link myVehicleRed2 = myCar.setLink(carColor, "myVehicleRed", myColor);
		assert Objects.equals(myVehicleRed1, myVehicleRed2);
	}

	public void testLinkWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Attribute power = car.setAttribute("Power");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myCar = car.newInstance("myCar");
		Generic myColor = color.newInstance("red");
		Link myVehicleRed = myCar.setLink(carColor, "Power", myColor);
		assert !Objects.equals(myVehicleRed, power);
	}
}
