package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.SuperRuleConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class LinkTypeCheckTest extends AbstractTest {

	public void malformedRelationInstanceWrongTargetType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		Type road = cache.addType("Road");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog, road);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		road.addInstance("myRoad");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}

	public void malformedRelationInstanceTooFewTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		Type road = cache.addType("Road");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog, road);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		road.addInstance("myRoad");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}

	public void relationInstanceTooMuchTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		Type road = cache.addType("Road");
		final Relation runsOver = vehicle.setRelation("RunsOver", dog, road);
		final Generic myBMW = vehicle.addInstance("myBMW");
		final Generic yourDog = dog.addInstance("yourDog");
		final Generic myRoad = road.addInstance("myRoad");

		try {
			myBMW.setLink(runsOver, "myBMWRunsOverYourDog", yourDog, myRoad, yourDog);
		} catch (IllegalStateException ignore) {

		}

	}

	public void malformedAttributeWrongBase() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type dog = cache.addType("Dog");
		final Attribute runs = vehicle.setAttribute("Runs");
		final Generic myBMW = vehicle.addInstance("myBMW");
		assert myBMW.inheritsFrom(runs.getBaseComponent());
		final Generic yourDog = dog.addInstance("yourDog");

		myBMW.setValue(runs, "myBMWRuns");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				yourDog.setValue(runs, "myDogRuns");
			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void test() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		assert myVehicle.getLink(vehicleColor, cache.setType("Date")) == null;
		assert myVehicle.getLinks(vehicleColor, cache.setType("Date")).size() == 0;
	}
}
