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
		final Relation runsOver = vehicle.setRelation( "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance( "myBMW");
		final Generic yourDog = dog.newInstance( "yourDog");
		road.newInstance( "myRoad");

		try {
			myBMW.setLink( runsOver, "myBMWRunsOverYourDog", yourDog, yourDog);
		} catch (IllegalStateException ignore) {

		}

	}

	public void malformedRelationInstanceTooFewTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation( "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance( "myBMW");
		final Generic yourDog = dog.newInstance( "yourDog");
		road.newInstance( "myRoad");

		try {
			myBMW.setLink( runsOver, "myBMWRunsOverYourDog", yourDog);
		} catch (IllegalStateException ignore) {

		}
	}

	public void relationInstanceTooMuchTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation( "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance( "myBMW");
		final Generic yourDog = dog.newInstance( "yourDog");
		final Generic myRoad = road.newInstance( "myRoad");

		try {
			myBMW.setLink( runsOver, "myBMWRunsOverYourDog", yourDog, myRoad, yourDog);
		} catch (IllegalStateException ignore) {

		}

	}

	public void malformedAttributeWrongBase() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Attribute runs = vehicle.setAttribute( "Runs");
		final Generic myBMW = vehicle.newInstance( "myBMW");
		assert myBMW.inheritsFrom(runs.getBaseComponent());
		final Generic yourDog = dog.newInstance( "yourDog");

		myBMW.setValue( runs, "myBMWRuns");
		try {
			yourDog.setValue( runs, "myDogRuns");
		} catch (IllegalStateException ignore) {

		}
	}

	public void test() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation( "vehicleColor", color);
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		Generic red = color.newInstance( "red");
		myVehicle.setLink( vehicleColor, "myVehicleRed", red);
		assert myVehicle.getLink( vehicleColor, cache.newType("Date")) == null;
		assert myVehicle.getLinks( vehicleColor, cache.newType("Date")).size() == 0;
	}
}
