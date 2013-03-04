package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class SingularConstraintTest extends AbstractTest {

	public void testSingularConstraint() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Relation metaRelation = cache.getMetaRelation();

		metaRelation.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert !metaRelation.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		metaRelation.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		metaRelation.disableSingularConstraint(cache, Statics.BASE_POSITION);
		assert !metaRelation.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		metaRelation.enableSingularConstraint(cache);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache);

		metaRelation.disableSingularConstraint(cache);
		assert !metaRelation.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		assert !metaRelation.isSingularConstraintEnabled(cache);
	}

	public void testConstraintCheckOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "Power");
		Attribute vehicleDriver = vehicle.setAttribute(cache, "Driver");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");

		vehiclePower.enableSingularConstraint(cache, 0);
		assert vehiclePower.isSingularConstraintEnabled(cache, 0);
		myVehicle.setValue(cache, vehiclePower, "5").remove(cache);
		myVehicle.setValue(cache, vehiclePower, "6");
		myVehicle.setValue(cache, vehicleDriver, "JC");
		cache.flush();
	}

	public void testConstraintCheckKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "Power");
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		vehiclePower.enableSingularConstraint(cache, 0);
		assert vehiclePower.isSingularConstraintEnabled(cache, 0);
		Holder myVehiclePowerValue1 = myVehicle.setValue(cache, vehiclePower, "5");
		assert myVehicle.getHolders(cache, vehiclePower).size() == 1;
		assert myVehicle.getHolders(cache, vehiclePower).get(0).equals(myVehiclePowerValue1);
		Holder myVehiclePowerValue2 = myVehicle.setValue(cache, vehiclePower, 2);
		assert myVehicle.getHolders(cache, vehiclePower).size() == 1;
		assert myVehicle.getHolders(cache, vehiclePower).get(0).equals(myVehiclePowerValue2);
	}

	public void testRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		runsOver.enableSingularConstraint(cache, 0);
		assert runsOver.isSingularConstraintEnabled(cache, 0);
		Link runsOverLink1 = myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		assert myBMW.getLink(cache, runsOver, yourDog).equals(runsOverLink1) : myBMW.getLink(cache, runsOver, yourDog);
		Link runsOverLink2 = myBMW.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
		assert !runsOverLink1.isAlive(cache);
		assert myBMW.getLink(cache, runsOver, yourDog).equals(runsOverLink2) : myBMW.getLink(cache, runsOver, yourDog);
	}

	public void testRelationWithTwoSingular() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		final Relation humanDriveVehicle = human.setRelation(cache, "Drive", vehicle);
		final Generic myck = human.newInstance(cache, "myck");
		final Generic nico = human.newInstance(cache, "nico");
		final Generic myckVehicle = vehicle.newInstance(cache, "myckVehicle");
		final Generic nicoVehicle = vehicle.newInstance(cache, "nicoVehicle");
		humanDriveVehicle.enableSingularConstraint(cache, Statics.BASE_POSITION);
		humanDriveVehicle.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		Link humanDriveVehicleLink1 = myck.setLink(cache, humanDriveVehicle, "myckDrive", myckVehicle);
		cache.flush();
		Link humanDriveVehicleLink2 = myck.setLink(cache, humanDriveVehicle, "myckDrive", nicoVehicle);
		assert !humanDriveVehicleLink1.isAlive(cache);
		assert myck.getLink(cache, humanDriveVehicle, nicoVehicle).equals(humanDriveVehicleLink2);
		nico.setLink(cache, humanDriveVehicle, "nicoDrive", myckVehicle);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
	}

	public void testDoubleRelationSimpleKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationSimpleOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic yourSecondDog = dog.newInstance(cache, "yourSecondDog");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		myBMW.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog);
	}

	public void testDoubleRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic yourSecondDog = dog.newInstance(cache, "yourSecondDog");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		myBMW.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog, myRoad);
	}

	public void testDoubleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic myRoad = road.newInstance(cache, "myRoad");
		final Generic myRoad2 = road.newInstance(cache, "myRoad2");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog, myRoad2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationKO2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		Type dogType = cache.newType("Dog");
		Type roadType = cache.newType("Road");
		final Relation runsOver = vehicleType.setRelation(cache, "RunsOver", dogType, roadType);
		final Generic vehicle = vehicleType.newInstance(cache, "myBMW");
		final Generic dog = dogType.newInstance(cache, "yourDog");
		final Generic dog2 = dogType.newInstance(cache, "yourSecondDog");
		final Generic road = roadType.newInstance(cache, "myRoad");

		runsOver.enableSingularConstraint(cache, 2);
		assert runsOver.isSingularConstraintEnabled(cache, 2);

		vehicle.setLink(cache, runsOver, "myBMWRunsOverYourDog", dog, road);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.setLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", dog2, road);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	// Rapportés depuis 2010 ----------------------------------------------------------------------

	public void noMoreThanOneAttributePerEntity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Attribute registration = car.setAttribute(cache, "registration");
		registration.enableSingularConstraint(cache);
		Holder myBmwRegistration1 = myBmw.setValue(cache, registration, "AB123CD");
		Holder myBmwRegistration2 = myBmw.setValue(cache, registration, "DC321BA");
		assert myBmw.getHolders(cache, registration).size() == 1;
		assert myBmw.getHolders(cache, registration).get(0).equals(myBmwRegistration2);
		assert !myBmwRegistration1.isAlive(cache);
	}

	// public void noMoreThanOneAttributePerEntity2() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type car = cache.newType("Car");
	// Type frenchCar = car.newSubType(cache, "frenchCar");
	// Attribute registration = car.addAttribute(cache, "registration");
	// final Attribute frenchRegistration = frenchCar.addSubAttribute(cache, registration, "French Registration");
	// registration.enableSingularConstraint(cache);
	// final Generic myBmw = frenchCar.newInstance(cache, "myBmw");
	// myBmw.setValue(cache, registration, "AB123CD");
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// myBmw.setValue(cache, frenchRegistration, "JAIME75");
	// }
	// }.assertIsCausedBy(SingularConstraintViolationException.class);
	// }

	public void singularForTargetAxe() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Type mother = cache.newType("mother");
				Type children = cache.newType("children");
				Relation myChildren = mother.setRelation(cache, "myChildren", children);
				myChildren.enableSingularConstraint(cache, Statics.TARGET_POSITION);

				Generic mama1 = mother.newInstance(cache, "mama1");
				Generic mama2 = mother.newInstance(cache, "mama2");
				Generic baby1 = children.newInstance(cache, "baby1");
				mama1.setLink(cache, myChildren, "mama1_baby1", baby1);
				Generic baby2 = children.newInstance(cache, "baby2");
				mama1.setLink(cache, myChildren, "mama1_baby2", baby2);
				mama2.setLink(cache, myChildren, "mama2_baby2", baby2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void singularForTargetAxe2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Type mother = cache.newType("mother");
				Type children = cache.newType("children");
				Relation myChildren = mother.setRelation(cache, "myChildren", children);
				myChildren.enableSingularConstraint(cache, Statics.TARGET_POSITION);

				Generic mama1 = mother.newInstance(cache, "mama1");
				Generic baby1 = children.newInstance(cache, "baby1");
				mama1.setLink(cache, myChildren, "mama1_baby1", baby1);
				Generic baby2 = children.newInstance(cache, "baby2");
				mama1.setLink(cache, myChildren, "mama1_baby2", baby2);
				mama1.setLink(cache, myChildren, "test", baby2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}
}
