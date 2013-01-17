package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.Statics;
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
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		Attribute vehicleDriver = vehicle.addAttribute(cache, "Driver");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");

		vehiclePower.enableSingularConstraint(cache, 0);
		assert vehiclePower.isSingularConstraintEnabled(cache, 0);
		myVehicle.addValue(cache, vehiclePower, "5").remove(cache);
		myVehicle.addValue(cache, vehiclePower, "6");
		myVehicle.addValue(cache, vehicleDriver, "JC");
		cache.flush();
	}

	public void testConstraintCheckKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");

		vehiclePower.enableSingularConstraint(cache, 0);
		assert vehiclePower.isSingularConstraintEnabled(cache, 0);

		myVehicle.addValue(cache, vehiclePower, "5");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myVehicle.addValue(cache, vehiclePower, 2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.addRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");

		runsOver.enableSingularConstraint(cache, 0);
		assert runsOver.isSingularConstraintEnabled(cache, 0);

		myBMW.addLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testRelationWithTwoSingular() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		final Relation humanDriveVehicle = human.addRelation(cache, "Drive", vehicle);
		final Generic myck = human.newInstance(cache, "myck");
		final Generic nico = human.newInstance(cache, "nico");
		final Generic myckVehicle = vehicle.newInstance(cache, "myckVehicle");
		final Generic nicoVehicle = vehicle.newInstance(cache, "nicoVehicle");

		humanDriveVehicle.enableSingularConstraint(cache, Statics.BASE_POSITION);
		humanDriveVehicle.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		myck.addLink(cache, humanDriveVehicle, "myckDrive", myckVehicle);
		cache.flush();

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myck.addLink(cache, humanDriveVehicle, "myckDrive", nicoVehicle);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				nico.addLink(cache, humanDriveVehicle, "nicoDrive", myckVehicle);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
	}

	public void testDoubleRelationSimpleKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.addRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.addLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationSimpleOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Relation runsOver = vehicle.addRelation(cache, "RunsOver", dog);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic yourSecondDog = dog.newInstance(cache, "yourSecondDog");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.addLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		myBMW.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog);
	}

	public void testDoubleRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.addRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic yourSecondDog = dog.newInstance(cache, "yourSecondDog");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.addLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		myBMW.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog, myRoad);
	}

	public void testDoubleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.addRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic myRoad = road.newInstance(cache, "myRoad");
		final Generic myRoad2 = road.newInstance(cache, "myRoad2");

		runsOver.enableSingularConstraint(cache, 1);
		assert runsOver.isSingularConstraintEnabled(cache, 1);

		myBMW.addLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog, myRoad2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationKO2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		Type dogType = cache.newType("Dog");
		Type roadType = cache.newType("Road");
		final Relation runsOver = vehicleType.addRelation(cache, "RunsOver", dogType, roadType);
		final Generic vehicle = vehicleType.newInstance(cache, "myBMW");
		final Generic dog = dogType.newInstance(cache, "yourDog");
		final Generic dog2 = dogType.newInstance(cache, "yourSecondDog");
		final Generic road = roadType.newInstance(cache, "myRoad");

		runsOver.enableSingularConstraint(cache, 2);
		assert runsOver.isSingularConstraintEnabled(cache, 2);

		vehicle.addLink(cache, runsOver, "myBMWRunsOverYourDog", dog, road);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.addLink(cache, runsOver, "myBMWRunsOverAndOverOverYourDog", dog2, road);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	// Rapportés depuis 2010 ----------------------------------------------------------------------

	public void noMoreThanOneAttributePerEntity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Attribute registration = car.addAttribute(cache, "registration");
		registration.enableSingularConstraint(cache);
		myBmw.addValue(cache, registration, "AB123CD");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.addValue(cache, registration, "DC321BA");
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

	}

	// public void noMoreThanOneAttributePerEntity2() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type car = cache.newType("Car");
	// Type frenchCar = car.newSubType(cache, "frenchCar");
	// Attribute registration = car.addAttribute(cache, "registration");
	// final Attribute frenchRegistration = frenchCar.addSubAttribute(cache, registration, "French Registration");
	// registration.enableSingularConstraint(cache);
	// final Generic myBmw = frenchCar.newInstance(cache, "myBmw");
	// myBmw.addValue(cache, registration, "AB123CD");
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// myBmw.addValue(cache, frenchRegistration, "JAIME75");
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
				Relation myChildren = mother.addRelation(cache, "myChildren", children);
				myChildren.enableSingularConstraint(cache, Statics.TARGET_POSITION);

				Generic mama1 = mother.newInstance(cache, "mama1");
				Generic mama2 = mother.newInstance(cache, "mama2");
				Generic baby1 = children.newInstance(cache, "baby1");
				mama1.addLink(cache, myChildren, "mama1_baby1", baby1);
				Generic baby2 = children.newInstance(cache, "baby2");
				mama1.addLink(cache, myChildren, "mama1_baby2", baby2);
				mama2.addLink(cache, myChildren, "mama2_baby2", baby2);
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
				Relation myChildren = mother.addRelation(cache, "myChildren", children);
				myChildren.enableSingularConstraint(cache, Statics.TARGET_POSITION);

				Generic mama1 = mother.newInstance(cache, "mama1");
				Generic baby1 = children.newInstance(cache, "baby1");
				mama1.addLink(cache, myChildren, "mama1_baby1", baby1);
				Generic baby2 = children.newInstance(cache, "baby2");
				mama1.addLink(cache, myChildren, "mama1_baby2", baby2);
				mama1.addLink(cache, myChildren, "test", baby2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}
}
