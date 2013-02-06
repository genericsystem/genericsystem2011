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
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		road.newInstance(cache, "myRoad");
		
		new RollbackCatcher() {
			
			@Override
			public void intercept() {
				myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}
	
	public void malformedRelationInstanceTooFewTargets() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		final Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		road.newInstance(cache, "myRoad");
		
		new RollbackCatcher() {
			
			@Override
			public void intercept() {
				myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog);
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}
	
	public void relationInstanceTooMuchTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		Type road = cache.newType("Road");
		Relation runsOver = vehicle.setRelation(cache, "RunsOver", dog, road);
		Generic myBMW = vehicle.newInstance(cache, "myBMW");
		Generic yourDog = dog.newInstance(cache, "yourDog");
		Generic myRoad = road.newInstance(cache, "myRoad");
		myBMW.setLink(cache, runsOver, "myBMWRunsOverYourDog", yourDog, myRoad, yourDog);
	}
	
	public void malformedAttributeWrongBase() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type dog = cache.newType("Dog");
		final Attribute runs = vehicle.setAttribute(cache, "Runs");
		final Generic myBMW = vehicle.newInstance(cache, "myBMW");
		final Generic yourDog = dog.newInstance(cache, "yourDog");
		
		myBMW.setValue(cache, runs, "myBMWRuns");
		new RollbackCatcher() {
			
			@Override
			public void intercept() {
				yourDog.setValue(cache, runs, "myDogRuns").log();
			}
		}.assertIsCausedBy(SuperRuleConstraintViolationException.class);
	}
}
