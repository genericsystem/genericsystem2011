package org.genericsystem.impl;

import java.util.Arrays;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class UniqueValueConstraintTest extends AbstractTest {

	// Tests d'heritage
	private void testHeritage(Type vehicle, Type truck) {
		assert vehicle.getSubTypes().contains(truck);
		assert truck.inheritsFrom(vehicle);
	}

	public void testPropertyMultiAttributeOnDerivedClassKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		final Type truck = vehicle.addSubType("truck");

		// plaque d'immatriculation d'un vehicule
		final Attribute registration = vehicle.setAttribute("Registration");
		registration.enableUniqueValueConstraint();
		final Attribute registration2 = truck.setAttribute("Registration");

		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic myTruck = truck.addInstance("myTruck");

		myVehicle.setValue(registration, "315DT75");
		myTruck.setValue(registration2, "315DT76");
		myTruck.setValue(registration2, "315DT77");

		assert myVehicle.getValues(registration).get(0).equals("315DT75");
		assert myVehicle.getValues(registration).size() == 1;

		assert myTruck.getValues(registration).get(0).equals("315DT76");
		assert myTruck.getValues(registration).get(1).equals("315DT77");
		assert myTruck.getValues(registration).size() == 2;

		assert myTruck.getValues(registration2).get(0).equals("315DT76");
		assert myTruck.getValues(registration2).get(1).equals("315DT77");
		assert myTruck.getValues(registration2).size() == 2;

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myTruck.setValue(registration2, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);

	}

	public void testPropertyMultiAttributeOnDerivedClassOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		final Type truck = vehicle.addSubType("truck");

		final Type superTruck = truck.addSubType("superTruck");

		// plaque d'immatriculation d'un vehicule
		final Attribute registration = vehicle.setAttribute("Registration");
		registration.enableUniqueValueConstraint();

		final Attribute registration3 = truck.setAttribute("Registration");
		registration3.disableUniqueValueConstraint();
		assert !registration3.isUniqueValueConstraintEnabled();

		final Attribute registration2 = truck.setAttribute("Registration");
		registration2.disableUniqueValueConstraint();
		assert !registration2.isUniqueValueConstraintEnabled();

		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic myTruck = truck.addInstance("myTruck");
		final Generic mySuperTruck = superTruck.addInstance("mySuperTruck");
		final Generic mySuperTruck2 = superTruck.addInstance("mySuperTruck2");

		myVehicle.setValue(registration, "315DT75");
		myTruck.setValue(registration2, "315DT76");
		myTruck.setValue(registration2, "315DT77");
		mySuperTruck.setValue(registration2, "315DT77");
		mySuperTruck.setValue(registration2, "315DT76");
		mySuperTruck2.setValue(registration2, "315DT76");
		mySuperTruck2.setValue(registration2, "315DT77");

		assert myVehicle.getValues(registration).get(0).equals("315DT75");
		assert myVehicle.getValues(registration).size() == 1;

		assert myTruck.getValues(registration).get(0).equals("315DT76");
		assert myTruck.getValues(registration).get(1).equals("315DT77");
		assert myTruck.getValues(registration).size() == 2;

		assert mySuperTruck.getValues(registration2).get(0).equals("315DT77");
		assert mySuperTruck.getValues(registration2).get(1).equals("315DT76");
		assert mySuperTruck.getValues(registration2).size() == 2;
		assert mySuperTruck2.getValues(registration2).get(0).equals("315DT76");
		assert mySuperTruck2.getValues(registration2).get(1).equals("315DT77");
		assert mySuperTruck2.getValues(registration2).size() == 2;
	}

	public void testPropertySimpleAttributeOnDerivedClassOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		final Type truck = vehicle.addSubType("truck");

		// plaque d'immatriculation d'un camion
		final Attribute registration = vehicle.setAttribute("Registration");
		final Attribute registration2 = truck.setAttribute("Registration");
		registration2.enableUniqueValueConstraint();

		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic myTruck = truck.addInstance("myTruck");

		myTruck.setValue(registration2, "315DT75");

		assert myTruck.getValue(registration2).equals("315DT75");

		myVehicle.setValue(registration, "315DT75");
		myVehicle.setValue(registration, "315DT76");

		assert myVehicle.getValues(registration).get(0).equals("315DT75");
		assert myVehicle.getValues(registration).get(1).equals("315DT76");
		assert myVehicle.getValues(registration).size() == 2;

		assert !registration.equals(registration2);

		assert myTruck.getValues(registration).get(0).equals("315DT75");
		assert myTruck.getValues(registration).size() == 1;

		assert registration.isAlive();
		assert myTruck.isAlive();
		assert myVehicle.isAlive();
	}

	public void testPropertySimpleAttributeFromBaseClassKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		// plaque d'immatriculation d'un vehicule
		final Attribute registration = vehicle.setAttribute("Registration");
		registration.enableUniqueValueConstraint();

		final Type truck = vehicle.addSubType("truck");

		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic myTruck = truck.addInstance("myTruck");

		// Tests d'heritage
		testHeritage(vehicle, truck);

		myTruck.setValue(registration, "315DT75");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.setValue(registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void testPropertySimpleAttributeFromDerivedClassKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		// plaque d'immatriculation d'un vehicule
		final Attribute registration = vehicle.setAttribute("Registration");
		registration.enableUniqueValueConstraint();

		final Type truck = vehicle.addSubType("truck");

		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic myTruck = truck.addInstance("myTruck");

		// Tests d'heritage
		testHeritage(vehicle, truck);

		myVehicle.setValue(registration, "315DT75");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myTruck.setValue(registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void testPropertySimpleAttributeByInheritanceKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");

		// plaque d'immatriculation d'un vehicule
		final Attribute registration = vehicle.setAttribute("Registration");
		registration.enableUniqueValueConstraint();

		Type car = vehicle.addSubType("car");
		Type truck = vehicle.addSubType("truck");

		final Generic myCar = car.addInstance("myCar");
		final Generic myTruck = truck.addInstance("myTruck");

		// Tests d'heritage
		assert vehicle.getSubTypes().containsAll(Arrays.asList(car, truck));
		assert car.inheritsFrom(vehicle);
		assert truck.inheritsFrom(vehicle);

		myCar.setValue(registration, "315DT75");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				// violation de l'unicite de la plaque d'immatriculation:
				// un camion et une voiture ne peuvent pas avoir la meme plaque d'immatriculation
				myTruck.setValue(registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute registration = car.setAttribute("Registration");
		registration.enableUniqueValueConstraint();
		final Generic myCar = car.addInstance("myCar");
		final Generic yourCar = car.addInstance("yourCar");
		myCar.setValue(registration, "315DT75");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setValue(registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void testPropertySimpleAttributeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute registration = car.setAttribute("Registration");
		registration.enableUniqueValueConstraint();
		final Generic myCar = car.addInstance("myCar");
		final Generic yourCar = car.addInstance("yourCar");
		myCar.setValue(registration, "315DT74");
		myCar.setValue(registration, "315DT75");
		yourCar.setValue(registration, "315DT76");
		assert myCar.getValues(registration).contains("315DT74");
		assert myCar.getValues(registration).contains("315DT75");
		assert yourCar.getValue(registration).equals("315DT76") : yourCar.getValue(registration);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type road = cache.addType("Road");
		Type human = cache.addType("Human");

		final Relation driving = car.setRelation("DrivingAlong", human, road);
		driving.enableUniqueValueConstraint();

		final Generic myCar = car.addInstance("myCar");
		final Generic myHuman = human.addInstance("myHuman");
		final Generic myRoad = road.addInstance("myRoad");
		final Generic yourCar = car.addInstance("yourCar");
		final Generic yourHuman = human.addInstance("yourHuman");
		final Generic yourRoad = road.addInstance("yourRoad");
		myCar.setLink(driving, "_MY_driving", myHuman, myRoad);
		yourCar.setLink(driving, "_YOUR_driving", yourHuman, yourRoad);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setLink(driving, "_MY_driving", yourHuman, yourRoad);
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}
}
