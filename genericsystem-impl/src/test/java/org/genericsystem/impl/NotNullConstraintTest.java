package org.genericsystem.impl;

import java.util.Arrays;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.PhantomConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
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
		myCar.setValue(cache, registration, null);
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

		assert myCar.getLinks(cache, driving).isEmpty();
		myCar.setLink(cache, driving, null, myHuman, myRoad);
		assert myCar.getLinks(cache, driving).isEmpty();
		Link test = myCar.setLink(cache, driving, "test", myHuman, myRoad);
		Link test2 = myCar.setLink(cache, driving, "test2", myHuman, myRoad);
		assert myCar.getLinks(cache, driving).containsAll(Arrays.asList(test, test2));
		myCar.setLink(cache, driving, null, myHuman, myRoad);// do nothing
		assert myCar.getLinks(cache, driving).containsAll(Arrays.asList(test, test2));
		final Cache superCache = cache.newSuperCache();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				driving.enableSingularConstraint(superCache);
			}
		};
		test2.remove(cache);
		driving.enableSingularConstraint(cache);
		myCar.setLink(cache, driving, null, myHuman, myRoad);// remove test
		assert myCar.getLinks(cache, driving).isEmpty();
	}

	@Test(groups = "subtyping")
	public void testEnabledConstraintOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newSubType(cache, null);
	}

	@Test(groups = "subtyping")
	public void testEnableThenDisableConstraintOnASimpleTypeThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		car.enableNotNullConstraint(cache);
		car.disableNotNullConstraint(cache);
		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);
		assert expected == actual;
	}

	@Test(groups = "subtyping")
	public void testEnableSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newSubType(cache, null);
	}

	@Test(groups = "subtyping")
	public void testDisabledSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		car.disableNotNullConstraint(cache);
		car.disableNotNullConstraint(cache);
		Type expected = car.newSubType(cache, null);
	}

	@Test(groups = "attribute")
	public void testConstraintIsDisabledByDefaultOnASimpleTypeThenCreateAnAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute(cache, "Registration");
		Holder myBmwPower = car.setAttribute(cache, 235);
		final Generic myBmw = car.newInstance(cache, "myBmw");
		myBmw.setValue(cache, registration, null);
		assert myBmw.getHolder(cache, registration) == null;
		assert myBmw.getValues(cache, registration).isEmpty();
		myBmw.setValue(cache, registration, null);// No exception
		assert myBmw.getHolder(cache, registration) == null;
		assert myBmw.getValues(cache, registration).isEmpty();
	}

	@Test(groups = "attribute")
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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myBus = vehicle.newInstance(cache, "myBus");

		Attribute carRegistration = car.setAttribute(cache, "carRegistration");
		final Attribute vehicleRegistration = vehicle.setAttribute(cache, "vehicleRegistration");
		carRegistration.enableNotNullConstraint(cache);

		myBmw.setValue(cache, carRegistration, "AA-BB-CC");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setValue(cache, vehicleRegistration, null);
			}
		}.assertIsCausedBy(PhantomConstraintViolationException.class);
	}
}
