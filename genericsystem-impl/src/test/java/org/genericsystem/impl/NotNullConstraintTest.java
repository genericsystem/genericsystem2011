package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.NotNullConstraintViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class NotNullConstraintTest extends AbstractTest {

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute(cache, "Registration");
		registration.enableNotNullConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.setValue(cache, registration, null);
			}
		}.assertIsCausedBy(NotNullConstraintViolationException.class);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.setRelation(cache, "DrivingAlong", human, road);
		driving.enableNotNullConstraint(cache);

		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.setLink(cache, driving, null, myHuman, myRoad);
			}
		}.assertIsCausedBy(NotNullConstraintViolationException.class);
	}

	@Test(groups = "subtyping")
	public void testConstraintIsDisabledByDefaultOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);
		assert expected == actual;
	}

	@Test(groups = "subtyping", expectedExceptions = RollbackException.class)
	public void testEnabledConstraintOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.enableNotNullConstraint(cache);
		car.newSubType(cache, null);
	}

	@Test(groups = "subtyping")
	public void testEnableThenDisableConstraintOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.enableNotNullConstraint(cache);
		car.disableNotNullConstraint(cache);

		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);
		assert expected == actual;
	}

	@Test(groups = "subtyping", expectedExceptions = RollbackException.class)
	public void testEnableSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");

		car.enableNotNullConstraint(cache);
		car.enableNotNullConstraint(cache);

		car.newSubType(cache, null);
	}

	@Test(groups = "subtyping")
	public void testDisabledSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.disableNotNullConstraint(cache);
		car.disableNotNullConstraint(cache);

		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);
		assert expected == actual;
	}

	@Test(groups = "attribute")
	public void testConstraintIsDisabledByDefaultOnASimpleTypeThenCreateAnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute registration = car.setAttribute(cache, "Registration");

		Type myBmw = car.newSubType(cache, "myBmw");
		myBmw.setValue(cache, registration, null);

		assert myBmw.getValue(cache, registration) == null;
	}

	@Test(groups = "attribute", expectedExceptions = RollbackException.class)
	public void testEnabledConstraintOnASimpleTypeThenCreateAnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute registration = car.setAttribute(cache, "Registration");
		registration.enableNotNullConstraint(cache);

		Type myBmw = car.newSubType(cache, "myBmw");
		myBmw.setValue(cache, registration, null);
	}

	@Test(groups = "attribute")
	public void testEnableConstraintOnAComplexeHierarchyOfTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myBus = vehicle.newInstance(cache, "myBus");

		Attribute carRegistration = car.setAttribute(cache, "carRegistration");
		Attribute vehicleRegistration = vehicle.setAttribute(cache, "vehicleRegistration");
		carRegistration.enableNotNullConstraint(cache);

		myBmw.setValue(cache, carRegistration, "AA-BB-CC");
		myBmw.setValue(cache, vehicleRegistration, null);
		myBus.setValue(cache, vehicleRegistration, null);

		assert myBmw.getValue(cache, carRegistration).equals("AA-BB-CC");
		assert myBmw.getValue(cache, vehicleRegistration) == null;
		assert myBus.getValue(cache, vehicleRegistration) == null;
	}
}
