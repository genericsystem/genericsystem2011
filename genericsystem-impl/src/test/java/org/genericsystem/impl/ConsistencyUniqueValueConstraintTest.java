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
public class ConsistencyUniqueValueConstraintTest extends AbstractTest {
	public void PropertySimpleAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute registration = car.setAttribute("Registration");
		Generic myCar = car.newInstance("myCar");
		myCar.setValue(registration, "315DT75");
		registration.enableUniqueValueConstraint();
	}

	public void PropertySimpleAttributeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Attribute registration = car.setAttribute("Registration");
		Generic myCar = car.newInstance("myCar");
		Generic yourCar = car.newInstance("yourCar");
		myCar.setValue(registration, "315DT75");
		yourCar.setValue(registration, "315DT75");
		new RollbackCatcher() {
			@Override
			public void intercept() {

				registration.enableUniqueValueConstraint();
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);
	}

	public void PropertySimpleRelationOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine()
				.start();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		final Relation driving = car.setRelation("DrivingAlong", human);
		Generic myCar = car.newInstance("myCar");
		Generic yourCar = car.newInstance("yourCar");
		Generic myHuman = human.newInstance("myHuman");
		Generic yourHuman = human.newInstance("yourHuman");
		myCar.setLink(driving, "my_driving", myHuman);
		yourCar.setLink(driving, "your_driving", yourHuman);
		driving.enableUniqueValueConstraint();
	}

	public void PropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine()
				.start();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		final Relation driving = car.setRelation("DrivingAlong", human);
		Generic myCar = car.newInstance("myCar");
		Generic yourCar = car.newInstance("yourCar");
		Generic myHuman = human.newInstance("myHuman");
		Generic yourHuman = human.newInstance("yourHuman");
		myCar.setLink(driving, "my_driving", myHuman);
		yourCar.setLink(driving, "your_driving", yourHuman);
		yourCar.setLink(driving, "my_driving", yourHuman);
		new RollbackCatcher() {
			@Override
			public void intercept() {

				driving.enableUniqueValueConstraint();
			}
		}.assertIsCausedBy(UniqueValueConstraintViolationException.class);

	}
}
