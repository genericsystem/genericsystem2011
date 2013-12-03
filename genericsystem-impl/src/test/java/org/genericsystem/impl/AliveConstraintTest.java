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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type human = cache.addType("Human");
		final Relation humanDriveCar = human.setRelation("Drive", car);
		final Generic myCar = car.addInstance("myCar");
		final Generic myHuman = human.addInstance("myHuman");
		myHuman.remove();
		assert !myHuman.isAlive();

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myHuman.setLink(humanDriveCar, "myHumanDriveCar", myCar);
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
	}

	public void testPropertySimpleRelationOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type human = cache.addType("Human");
		Relation humanDriveCar = human.setRelation("Drive", car);
		Generic myCar = car.addInstance("myCar");
		Generic myHuman = human.addInstance("myHuman");

		assert myHuman.isAlive();
		myHuman.setLink(humanDriveCar, "myHumanDriveCar", myCar);
		assert myHuman.isAlive();
	}
}
