package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class LinkTypeCheckTest extends AbstractTest {

	public void malformedRelationInstanceWrongTargetType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		road.newInstance(cache, "myRoad");

		try {
			myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, yourDog);
		} catch (IllegalStateException ignore) {

		}

	}

	public void malformedRelationInstanceTooFewTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		road.newInstance(cache, "myRoad");

		try {
			myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
		} catch (IllegalStateException ignore) {

		}
	}

	public void relationInstanceTooMuchTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		try {
			myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad, yourDog);
		} catch (IllegalStateException ignore) {

		}

	}

	public void malformedAttributeWrongBase() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Attribute runs = vehicle.setAttribute(cache, "Runs");
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		assert myBMW.inheritsFrom(runs.getBaseComponent());
		final Generic yourDog = dog.newInstance(cache, "yourDog");

		myBMW.setValue(cache, runs, "myBMWRuns");
		try {
			yourDog.setValue(cache, runs, "myDogRuns");
		} catch (IllegalStateException ignore) {

		}
	}

	public void test() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "vehicleColor", color);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		assert myVehicle.getLink(cache, vehicleColor, cache.newType("Date")) == null;
		assert myVehicle.getLinks(cache, vehicleColor, cache.newType("Date")).size() == 0;
	}
}
