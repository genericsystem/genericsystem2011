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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Relation metaRelation = cache.getMetaRelation();

		metaRelation.enableSingularConstraint(Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert !metaRelation.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		metaRelation.enableSingularConstraint(Statics.TARGET_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		metaRelation.disableSingularConstraint(Statics.BASE_POSITION);
		assert !metaRelation.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		metaRelation.enableSingularConstraint();
		assert metaRelation.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		assert metaRelation.isSingularConstraintEnabled();

		metaRelation.disableSingularConstraint();
		assert !metaRelation.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert metaRelation.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		assert !metaRelation.isSingularConstraintEnabled();
	}

	public void testHeritageSingularOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.setAttribute("power");
		power.enableSingularConstraint();
		vehicle.addInstance("myVehicle");
		vehicle.setHolder(power, 123);
		Type car = vehicle.addSubType("Car");
		assert car.supers().size() == 1;
		car.addInstance("myCar");
	}

	public void testConstraintCheckOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("Power");
		Attribute vehicleDriver = vehicle.setAttribute("Driver");
		Generic myVehicle = vehicle.addInstance("myVehicle");

		vehiclePower.enableSingularConstraint(0);
		assert vehiclePower.isSingularConstraintEnabled(0);
		myVehicle.setValue(vehiclePower, "5").remove();
		myVehicle.setValue(vehiclePower, "6");
		myVehicle.setValue(vehicleDriver, "JC");
		cache.flush();
	}

	public void testConstraintCheckOK2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute vehiclePower = vehicle.setProperty("Power");
		car.setValue(vehiclePower, 50);
		assert vehicle.getValue(vehiclePower) == null;
		assert vehiclePower.isPropertyConstraintEnabled();
		assert car.getValue(vehiclePower).equals(50);
	}

	public void testConstraintCheckOK3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute vehiclePower = vehicle.setProperty("Power");
		vehicle.setValue(vehiclePower, 125);
		car.setValue(vehiclePower, 50);
		vehicle.setValue(vehiclePower, 250);
		assert car.getValue(vehiclePower).equals(50) : car.getValue(vehiclePower);
	}

	public void testConstraintCheckKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute("Power");
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		vehiclePower.enableSingularConstraint();
		assert vehiclePower.isSingularConstraintEnabled();
		Holder myVehiclePowerValue1 = myVehicle.setValue(vehiclePower, "5");
		assert myVehicle.getHolders(vehiclePower).size() == 1;
		assert myVehicle.getHolders(vehiclePower).get(0).equals(myVehiclePowerValue1);
		Holder myVehiclePowerValue2 = myVehicle.setValue(vehiclePower, 2);
		assert myVehicle.getHolders(vehiclePower).size() == 1;
		assert myVehicle.getHolders(vehiclePower).get(0).equals(myVehiclePowerValue2);
	}

	public void testConsistency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute("Power");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(vehiclePower, 123);
		myVehicle.setValue(vehiclePower, 50);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehiclePower.enableSingularConstraint(Statics.BASE_POSITION);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Color");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		runsOver.enableSingularConstraint(0);
		assert runsOver.isSingularConstraintEnabled(0);
		Link runsOverLink1 = myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog);
		assert myBMW.getLink(runsOver, yourDog).equals(runsOverLink1) : myBMW.getLink(runsOver, yourDog);
		Link runsOverLink2 = myBMW.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
		assert !runsOverLink1.isAlive() : runsOverLink1.info() + " / " + runsOverLink2.info();
		assert myBMW.getLink(runsOver, yourDog).equals(runsOverLink2) : myBMW.getLink(runsOver, yourDog);
	}

	public void testRelationWithTwoSingular() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		final Relation humanDriveVehicle = human.setRelation("Drive", vehicle);
		final Generic myck = human.addInstance("myck");
		final Generic nico = human.addInstance("nico");
		final Generic myckVehicle = vehicle.addInstance("myckVehicle");
		final Generic nicoVehicle = vehicle.addInstance("nicoVehicle");
		humanDriveVehicle.enableSingularConstraint(Statics.BASE_POSITION);
		humanDriveVehicle.enableSingularConstraint(Statics.TARGET_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		Link humanDriveVehicleLink1 = myck.setLink(humanDriveVehicle, "myckDrive", myckVehicle);
		cache.flush();
		Link humanDriveVehicleLink2 = myck.setLink(humanDriveVehicle, "myckDrive", nicoVehicle);
		assert !humanDriveVehicleLink1.isAlive();
		assert humanDriveVehicleLink2.isAlive();
		assert myck.getLink(humanDriveVehicle).equals(humanDriveVehicleLink2);
		assert myck.getLink(humanDriveVehicle, nicoVehicle).equals(humanDriveVehicleLink2);
		nico.setLink(humanDriveVehicle, "nicoDrive", myckVehicle);
		assert humanDriveVehicle.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert humanDriveVehicle.isSingularConstraintEnabled(Statics.TARGET_POSITION);
	}

	public void testDoubleRelationSimpleKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");

		runsOver.enableSingularConstraint(1);
		assert runsOver.isSingularConstraintEnabled(1);

		myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationSimpleOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		final Generic yourSecondDog = dog.addInstance("yourSecondDog");

		runsOver.enableSingularConstraint(1);
		assert runsOver.isSingularConstraintEnabled(1);

		myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog);
		myBMW.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog);
	}

	public void testDoubleRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		Type road = cache.addType("Road");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog, road);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		final Generic yourSecondDog = dog.addInstance("yourSecondDog");
		final Generic myRoad = road.addInstance("myRoad");

		runsOver.enableSingularConstraint(1);
		assert runsOver.isSingularConstraintEnabled(1);

		myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		myBMW.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", yourSecondDog, myRoad);
	}

	public void testDoubleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		Type road = cache.addType("Road");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog, road);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		final Generic myRoad = road.addInstance("myRoad");
		final Generic myRoad2 = road.addInstance("myRoad2");

		runsOver.enableSingularConstraint(1);
		assert runsOver.isSingularConstraintEnabled(1);

		myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog, myRoad);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", yourDog, myRoad2);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testDoubleRelationKO2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicleType = cache.addType("Vehicle");
		Type dogType = cache.addType("Dog");
		Type roadType = cache.addType("Road");
		final Relation runsOver = vehicleType.setRelation("RunsOver", dogType, roadType);
		final Generic vehicle = vehicleType.addInstance("myBMW");
		final Generic dog = dogType.addInstance("yourDog");
		final Generic dog2 = dogType.addInstance("yourSecondDog");
		final Generic road = roadType.addInstance("myRoad");

		runsOver.enableSingularConstraint(2);
		assert runsOver.isSingularConstraintEnabled(2);

		vehicle.setLink(runsOver, "myBMWRunsOverYourDog", dog, road);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.setLink(runsOver, "myBMWRunsOverAndOverOverYourDog", dog2, road);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	// Rapportés depuis 2010 ----------------------------------------------------------------------

	public void noMoreThanOneAttributePerEntity() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBmw = car.addInstance("myBmw");
		Attribute registration = car.setAttribute("registration");
		registration.enableSingularConstraint();
		Holder myBmwRegistration1 = myBmw.setValue(registration, "AB123CD");
		Holder myBmwRegistration2 = myBmw.setValue(registration, "DC321BA");
		assert myBmw.getHolders(registration).size() == 1;
		assert myBmw.getHolders(registration).get(0).equals(myBmwRegistration2);
		assert !myBmwRegistration1.isAlive();
	}

	public void addSingularAndSubTypeTestOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.enableSingularConstraint();
		assert vehicleColor.isSingularConstraintEnabled();

		Type car = vehicle.addSubType("Car");
		// Relation carColor = ((GenericImpl)car).setSubRelation(vehicleColor,"carColor", color);

		Relation carColor = car.setRelation("vehicleColor", color);
		assert carColor.isSingularConstraintEnabled();

		Generic myCar = car.addInstance("myCar");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		Link myCarRed = myCar.setLink(carColor, "myCarRed", red);
		Link myCarYellow = myCar.setLink(carColor, "myCarYellow", yellow);

		assert !myCarRed.isAlive();
		assert myCarYellow.isAlive();
		assert myCar.getLinks(carColor).size() == 1 : myCar.getLinks(carColor);
		assert myCar.getLink(carColor) == myCarYellow;
	}

	public void deactivateSingularONSubTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");

		vehicle.setRelation("vehicleColor", color).enableSingularConstraint();

		Type car = vehicle.addSubType("Car");
		Relation carColor = car.setRelation("vehicleColor", color).disableSingularConstraint();

		Generic myCar = car.addInstance("myCar");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		Link myCarRed = myCar.setLink(carColor, "myCarRed", red);
		Link myCarYellow = myCar.setLink(carColor, "myCarYellow", yellow);

		assert myCarRed.isAlive();
		assert myCarYellow.isAlive();
		assert myCar.getLinks(carColor).size() == 2 : myCar.getLinks(carColor);
		assert myCar.getLinks(carColor).get(0) == myCarRed;
		assert myCar.getLinks(carColor).get(1) == myCarYellow;
	}

	public void singularForTargetAxe() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("car");
		Type color = cache.addType("color");
		final Relation carColor = car.setRelation("carColor", color).enableSingularConstraint(Statics.TARGET_POSITION);
		Generic myBmw = car.addInstance("myBmw");
		final Generic myAudi = car.addInstance("myAudi");
		final Generic yellow = color.addInstance("yellow");
		myBmw.setLink(carColor, "myBmwYellow", yellow);
		assert carColor.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myAudi.setLink(carColor, "myAudiYellow", yellow);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void singularForTargetAxe2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("vehicle");
		Type color = cache.addType("color");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color).enableSingularConstraint(Statics.TARGET_POSITION);
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		myVehicle.setLink(vehicleColor, "myCarRed", red);
		final Generic yellow = color.addInstance("yellow");
		myVehicle.setLink(vehicleColor, "myCarYellow", yellow);
		new RollbackCatcher() {
			@Override
			public void intercept() {

				myVehicle.setLink(vehicleColor, "myCarYellow2", yellow);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}
}
