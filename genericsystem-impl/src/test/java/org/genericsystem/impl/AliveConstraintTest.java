package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AliveConstraintTest extends AbstractTest {

	public void testPropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		final Relation humanDriveCar = human.setRelation(cache, "Drive", car);
		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic myHuman = human.newInstance(cache, "myHuman");
		myHuman.remove(cache);
		assert !myHuman.isAlive(cache);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myHuman.setLink(cache, humanDriveCar, "myHumanDriveCar", myCar);
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
	}

	public void testPropertySimpleRelationOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation humanDriveCar = human.setRelation(cache, "Drive", car);
		Generic myCar = car.newInstance(cache, "myCar");
		Generic myHuman = human.newInstance(cache, "myHuman");

		assert myHuman.isAlive(cache);
		myHuman.setLink(cache, humanDriveCar, "myHumanDriveCar", myCar);
		assert myHuman.isAlive(cache);
	}
}
