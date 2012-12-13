package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.SuperRuleConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class LinkTypeCheckTest extends AbstractTest {

	public void malformedRelationInstanceWrongTargetType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.addRelation(cache,"RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache,"myBMW");
		final Generic yourDog = dog.newInstance(cache,"yourDog");
		road.newInstance(cache,"myRoad");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myBMW.addLink(cache,runsOver, "myBMWRunsOverYourDog", yourDog, yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}

	public void malformedRelationInstanceTooFewTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.addRelation(cache,"RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache,"myBMW");
		final Generic yourDog = dog.newInstance(cache,"yourDog");
		road.newInstance(cache,"myRoad");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myBMW.addLink(cache,runsOver, "myBMWRunsOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}

	public void relationInstanceTooMuchTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		Relation runsOver = vehicle.addRelation(cache,"RunsOver", dog, road);
		Generic myBMW = vehicle.newInstance(cache,"myBMW");
		Generic yourDog = dog.newInstance(cache,"yourDog");
		Generic myRoad = road.newInstance(cache,"myRoad");
		myBMW.addLink(cache,runsOver, "myBMWRunsOverYourDog", yourDog, myRoad, yourDog);
	}

	public void malformedAttributeWrongBase() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Attribute runs = vehicle.addAttribute(cache,"Runs");
		final Generic myBMW = vehicle.newInstance(cache,"myBMW");
		final Generic yourDog = dog.newInstance(cache,"yourDog");

		myBMW.addValue(cache,runs, "myBMWRuns");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourDog.addValue(cache,runs, "myDogRuns");
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}
}
