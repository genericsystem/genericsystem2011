package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type car2 = cache.newType("Car");
		assert Objects.equals(car, car2);
	}

	public void testAttributeWithSameNameAsTypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		cache.newType("Plane");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.setAttribute(cache, "Plane");
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	public void testRelationWithSameNameAsTypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.setRelation(cache, "Plane", plane);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	// Attribute

	public void testAttributeWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute wheel = car.setAttribute(cache, "Wheels");
		Attribute wheel2 = car.setAttribute(cache, "Wheels");
		assert Objects.equals(wheel, wheel2);
	}

	public void testRelationWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type plane = cache.newType("Plane");
		Attribute wheelAttribute = car.setAttribute(cache, "Wheels");
		Relation wheelRelation = car.setRelation(cache, "Wheels", plane);
		assert !Objects.equals(wheelAttribute, wheelRelation);
	}

	// Relation

	public void testRelationWithSameNameAsRelationOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Relation carColor2 = car.setRelation(cache, "CarColor", color);
		assert Objects.equals(carColor, carColor2);
	}

	// 2 types

	public void testTwoTypesWithSameAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");
		car.setAttribute(cache, "Power");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				plane.setAttribute(cache, "Power");
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	public void testTwoTypesWithSameRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Type plane = cache.newType("Plane");
		final Type color = cache.newType("Color");
		car.setRelation(cache, "ColorRelation", color);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				plane.setRelation(cache, "ColorRelation", color);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	// Instance

	public void testInstanceWithSameNameAsInstanceOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance(cache, "myCar");
		Generic myCar2 = car.newInstance(cache, "myCar");
		assert Objects.equals(myCar, myCar2);
	}

	public void testInstanceWithSameNameAsTypeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		Generic myCar = car.newInstance(cache, "Car");
		assert !Objects.equals(car, myCar);
	}

	// Holder

	public void testHolderWithSameValueAsHolderOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		Generic myCar = car.newInstance(cache, "myCar");
		Holder myPower1 = myCar.setValue(cache, power, 20000);
		Holder myPower2 = myCar.setValue(cache, power, 20000);
		assert Objects.equals(myPower1, myPower2);
	}

	public void testHolderWithSameValueAsTypeNameKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "Power");
		Generic myCar = car.newInstance(cache, "myCar");
		Holder myPower = myCar.setValue(cache, power, "Car");
		assert !Objects.equals(myPower, car);
	}

	// Link

	public void testLinkWithSameNameAsLinkOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic myCar = car.newInstance(cache, "myCar");
		Generic myColor = color.newInstance(cache, "red");
		Link myVehicleRed1 = myCar.setLink(cache, carColor, "myVehicleRed", myColor);
		Link myVehicleRed2 = myCar.setLink(cache, carColor, "myVehicleRed", myColor);
		assert Objects.equals(myVehicleRed1, myVehicleRed2);
	}

	public void testLinkWithSameNameAsAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Attribute power = car.setAttribute(cache, "Power");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic myCar = car.newInstance(cache, "myCar");
		Generic myColor = color.newInstance(cache, "red");
		Link myVehicleRed = myCar.setLink(cache, carColor, "Power", myColor);
		assert !Objects.equals(myVehicleRed, power);
	}
}
