package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.NotNullConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class NotNullConstraintTest extends AbstractTest {

	public void testPropertySimpleAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute(cache, "Registration");
		registration.enableNotNullConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.setValue(cache, registration, null);
			}
		}.assertIsCausedBy(NotNullConstraintViolationException.class);
	}

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type road = cache.newType("Road");
		Type human = cache.newType("Human");

		final Relation driving = car.setRelation(cache, "DrivingAlong", human, road);
		driving.enableNotNullConstraint(cache);

		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		final Generic myRoad = road.newInstance(cache, "myRoad");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.setLink(cache, driving, null, myHuman, myRoad);
			}
		}.assertIsCausedBy(NotNullConstraintViolationException.class);
	}

	// public void testPropertyInheritedRelationKO() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type car = cache.newType("Car");
	// Type road = cache.newType("Road");
	// Type human = cache.newType("Human");
	//
	// final Relation possessing = car.addRelation(cache, "Possessing", human, road);
	// possessing.enableNotNullConstraint(cache);
	// final Relation driving = car.addSubRelation(cache, possessing, "DrivingAlong", human, road);
	//
	// final Generic myCar = car.newInstance(cache, "myCar");
	// final Generic myHuman = human.newInstance(cache, "myHuman");
	// final Generic myRoad = road.newInstance(cache, "myRoad");
	//
	// new RollbackCatcher() {
	//
	// @Override
	// public void intercept() {
	// myCar.setLink(cache, driving, null, myHuman, myRoad);
	// }
	// }.assertIsCausedBy(NotNullConstraintViolationException.class);
	// }

}
