package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Objects;
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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute registration = car.setAttribute("Registration");
		registration.enableRequiredConstraint();
		final Generic myCar = car.addInstance("myCar");
		myCar.setValue(registration, null);
	}

	public void testPropertySimpleProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type sportCar = car.addSubType("Sportcar");
		Attribute vehiclePower = vehicle.setProperty("Power");
		car.setValue(vehiclePower, 80);
		sportCar.setValue(vehiclePower, 250);
		assert car.getValue(vehiclePower).equals(80);
		assert sportCar.getValue(vehiclePower).equals(250);
		assert vehiclePower.isSingularConstraintEnabled();
		car.setValue(vehiclePower, 90);
		assert car.getValue(vehiclePower).equals(90);
		assert sportCar.getValue(vehiclePower).equals(250);
		sportCar.cancelAll(vehiclePower);
		assert Objects.equals(null, sportCar.getValue(vehiclePower)) : sportCar.getHolders(vehiclePower);
		sportCar.setValue(vehiclePower, 250);
		sportCar.setValue(vehiclePower, null);
		assert sportCar.getValue(vehiclePower) == null : sportCar.getValue(vehiclePower);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type road = cache.addType("Road");
		Type human = cache.addType("Human");

		final Relation driving = car.setRelation("DrivingAlong", human, road);
		// driving.enableRequiredConstraint();

		final Generic myCar = car.addInstance("myCar");
		final Generic myHuman = human.addInstance("myHuman");
		final Generic myRoad = road.addInstance("myRoad");

		assert myCar.getLinks(driving).isEmpty();
		myCar.setLink(driving, null, myHuman, myRoad);
		assert myCar.getLink(driving).getValue() == null;
		Link test = myCar.setLink(driving, "test", myHuman, myRoad);
		Link test2 = myCar.setLink(driving, "test2", myHuman, myRoad);
		assert myCar.getLinks(driving).containsAll(Arrays.asList(test, test2));
		myCar.cancelAll(driving, myHuman, myRoad);
		assert myCar.getLinks(driving).isEmpty() : myCar.getLinks(driving);
		test = myCar.setLink(driving, "test", myHuman, myRoad);
		test2 = myCar.setLink(driving, "test2", myHuman, myRoad);
		cache.mountNewCache();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				driving.enableSingularConstraint();
			}
		};
		test2.remove();
		driving.enableSingularConstraint();
		myCar.setLink(driving, null, myHuman, myRoad);
		assert myCar.getLink(driving).getValue() == null;
	}

	public void testEnabledConstraintOnASimpleTypeThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		car.addSubType(null);
	}

	public void testEnableThenDisableConstraintOnASimpleTypeThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		Type expected = car.addSubType(null);
		Generic actual = car.getAllSubType(null);
		assert expected == actual;
	}

	public void testEnableSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		car.addSubType(null);
	}

	public void testDisabledSeveralTimesConstraintOnASimpleTypeHasNoSideEffectThenCreateASubtype() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		car.addSubType(null);
	}

	public void testConstraintIsDisabledByDefaultOnASimpleTypeThenCreateAnAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute registration = car.setProperty("Registration");
		final Generic myBmw = car.addInstance("myBmw");
		Holder holder = car.setValue(registration, 235);
		myBmw.setValue(holder, null);
		assert myBmw.getValue(registration) == null : myBmw.getHolder(registration);
		assert !myBmw.getValues(registration).isEmpty();
		myBmw.setValue(registration, null);// No exception
		assert myBmw.getValue(registration) == null : myBmw.getHolder(registration);
	}

	public void testEnabledConstraintOnASimpleTypeThenCreateAnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute registration = vehicle.setAttribute("Registration");
		Type car = vehicle.addSubType("Car");
		car.setValue(registration, null);
	}

	public void testEnableConstraintOnAComplexeHierarchyOfTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic myBmw = car.addInstance("myBmw");
		vehicle.addInstance("myBus");

		Attribute carRegistration = car.setAttribute("vehicleRegistration");
		carRegistration.log();
		Attribute vehicleRegistration = vehicle.setAttribute("vehicleRegistration");
		carRegistration = ((GenericImpl) carRegistration).<Attribute> reFind();
		carRegistration.enableSingularConstraint();
		Holder value = myBmw.setValue(carRegistration, "AA-BB-CC");
		assert myBmw.getHolders(vehicleRegistration).contains(value);
		assert value.isAlive();
		assert myBmw.getHolders(vehicleRegistration).contains(value);
		myBmw.setValue(carRegistration, null);
		assert !value.isAlive();
		assert myBmw.getValue(vehicleRegistration) == null;
	}
}
