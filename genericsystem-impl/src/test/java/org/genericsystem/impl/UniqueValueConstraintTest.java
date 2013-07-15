package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class UniqueValueConstraintTest extends AbstractTest {

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute( "Registration");
		registration.enableUniqueValueConstraint();
		final Generic myCar = car.newInstance( "myCar");
		final Generic yourCar = car.newInstance( "yourCar");
		myCar.setValue( registration, "315DT75");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setValue( registration, "315DT75");
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.setRelation( "DrivingAlong", human, road);
		driving.enableUniqueValueConstraint();

		final Generic myCar = car.newInstance( "myCar");
		final Generic myHuman = human.newInstance( "myHuman");
		final Generic myRoad = road.newInstance( "myRoad");
		final Generic yourCar = car.newInstance( "yourCar");
		final Generic yourHuman = human.newInstance( "yourHuman");
		final Generic yourRoad = road.newInstance( "yourRoad");
		myCar.setLink( driving, "_MY_driving", myHuman, myRoad);
		yourCar.setLink( driving, "_YOUR_driving", yourHuman, yourRoad);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yourCar.setLink( driving, "_MY_driving", yourHuman, yourRoad);
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}
}
