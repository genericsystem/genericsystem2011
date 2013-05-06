package org.genericsystem.impl;

import java.util.Arrays;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
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
		registration.enableRequiredConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		myCar.setValue(cache, registration, null);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.setRelation(cache, "DrivingAlong", human, road);
		driving.enableRequiredConstraint(cache);

		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		assert myCar.getLinks(cache, driving).isEmpty();
		myCar.setLink(cache, driving, null, myHuman, myRoad);
		assert myCar.getLinks(cache, driving).isEmpty();
		Link test = myCar.setLink(cache, driving, "test", myHuman, myRoad);
		Link test2 = myCar.setLink(cache, driving, "test2", myHuman, myRoad);
		assert myCar.getLinks(cache, driving).containsAll(Arrays.asList(test, test2));
		myCar.clear(cache, driving, myHuman, myRoad);
		assert myCar.getLinks(cache, driving).isEmpty();
		test = myCar.setLink(cache, driving, "test", myHuman, myRoad);
		test2 = myCar.setLink(cache, driving, "test2", myHuman, myRoad);
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

	@Test
	public void testEnabledConstraintOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newSubType(cache, null);
	}

	@Test
	public void testEnableThenDisableConstraintOnASimpleTypeThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);
		assert expected == actual;
	}

	@Test
	public void testEnableSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newSubType(cache, null);
	}

	@Test
	public void testDisabledSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		Type expected = car.newSubType(cache, null);
	}

	@Test
	public void testConstraintIsDisabledByDefaultOnASimpleTypeThenCreateAnAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.setProperty(cache, "Registration");
		final Generic myBmw = car.newInstance(cache, "myBmw");
		Holder holder = car.setValue(cache, registration, 235);
		myBmw.setValue(cache, holder, null);
		assert myBmw.getHolder(cache, registration) == null;
		assert myBmw.getValues(cache, registration).isEmpty();
		myBmw.setValue(cache, registration, null);// No exception
		assert myBmw.getHolder(cache, registration) == null;
		assert myBmw.getValues(cache, registration).isEmpty();
	}

	@Test
	public void testEnabledConstraintOnASimpleTypeThenCreateAnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute registration = car.setAttribute(cache, "Registration");
		Type myBmw = car.newSubType(cache, "myBmw");
		myBmw.setValue(cache, registration, null);
	}

	@Test
	public void testEnableConstraintOnAComplexeHierarchyOfTypes() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myBus = vehicle.newInstance(cache, "myBus");

		Attribute carRegistration = car.setAttribute(cache, "vehicleRegistration");
		final Attribute vehicleRegistration = vehicle.setAttribute(cache, "vehicleRegistration");
		carRegistration = ((GenericImpl) carRegistration).<Attribute> reFind(cache);
		carRegistration.enableSingularConstraint(cache);
		Holder value = myBmw.setValue(cache, carRegistration, "AA-BB-CC");
		assert myBmw.getHolders(cache, vehicleRegistration).contains(value);
		assert value.isAlive(cache);
		assert myBmw.getHolders(cache, vehicleRegistration).contains(value);
		myBmw.setValue(cache, carRegistration, null);
		assert !value.isAlive(cache);
		assert myBmw.getHolders(cache, vehicleRegistration).isEmpty();
	}
}
