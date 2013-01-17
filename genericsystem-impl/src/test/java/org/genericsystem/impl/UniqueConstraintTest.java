package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.UniqueConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class UniqueConstraintTest extends AbstractTest {

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.addAttribute(cache, "Registration");
		registration.enableUniqueConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic yourCar = car.newInstance(cache, "yourCar");
		myCar.addValue(cache, registration, "315DT75");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.addValue(cache, registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueConstraintViolationException.class);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.addRelation(cache, "DrivingAlong", human, road);
		driving.enableUniqueConstraint(cache);

		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		final Generic myRoad = road.newInstance(cache, "myRoad");
		final Generic yourCar = car.newInstance(cache, "yourCar");
		final Generic yourHuman = human.newInstance(cache, "yourHuman");
		final Generic yourRoad = road.newInstance(cache, "yourRoad");
		myCar.addLink(cache, driving, "_MY_driving", myHuman, myRoad);
		yourCar.addLink(cache, driving, "_YOUR_driving", yourHuman, yourRoad);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.addLink(cache, driving, "_MY_driving", yourHuman, yourRoad);
			}
		}.assertIsCausedBy(UniqueConstraintViolationException.class);
	}

	// public void testPropertyInheritedRelationKO() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type car = cache.newType("Car");
	// Type road = cache.newType("Road");
	// Type human = cache.newType("Human");
	//
	// final Relation being = car.addRelation(cache,"BeingOn", human, road);
	// final Relation driving = car.addSubRelation(cache,being, "DrivingAlong", human, road);
	// being.enableUniqueConstraint(cache);
	//
	// final Generic myCar = car.newInstance(cache,"myBMW");
	// final Generic myHuman = human.newInstance(cache,"myHuman");
	// final Generic myRoad = road.newInstance(cache,"myRoad");
	// final Generic yourCar = car.newInstance(cache,"yourBMW");
	// final Generic yourHuman = human.newInstance(cache,"yourHuman");
	// final Generic yourRoad = road.newInstance(cache,"yourRoad");
	// myCar.addLink(cache,being, "_MY_driving", myHuman, myRoad);
	// yourCar.addLink(cache,driving, "_YOUR_driving", yourHuman, yourRoad);
	//
	// new RollbackCatcher() {
	//
	// @Override
	// public void intercept() {
	// yourCar.addLink(cache,driving, "_MY_driving", yourHuman, yourRoad);
	// }
	// }.assertIsCausedBy(UniqueConstraintViolationException.class);
	// }
}
