package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.UniqueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class UniqueConstraintTest extends AbstractTest {

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute(cache, "Registration");
		registration.enableUniqueConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic yourCar = car.newInstance(cache, "yourCar");
		myCar.setValue(cache, registration, "315DT75");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setValue(cache, registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueConstraintViolationException.class);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.setRelation(cache, "DrivingAlong", human, road);
		driving.enableUniqueConstraint(cache);

		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		final Generic myRoad = road.newInstance(cache, "myRoad");
		final Generic yourCar = car.newInstance(cache, "yourCar");
		final Generic yourHuman = human.newInstance(cache, "yourHuman");
		final Generic yourRoad = road.newInstance(cache, "yourRoad");
		myCar.setLink(cache, driving, "_MY_driving", myHuman, myRoad);
		yourCar.setLink(cache, driving, "_YOUR_driving", yourHuman, yourRoad);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setLink(cache, driving, "_MY_driving", yourHuman, yourRoad);
			}
		}.assertIsCausedBy(UniqueConstraintViolationException.class);
	}
}
