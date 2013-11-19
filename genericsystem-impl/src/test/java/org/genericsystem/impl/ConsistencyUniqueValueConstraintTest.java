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
		Type vehicle = cache.addType("Vehicle");
		Attribute registration = vehicle.setAttribute("Registration");
		Generic myCar = vehicle.newInstance("myCar");
		myCar.setValue(registration, "315DT75");
		registration.enableUniqueValueConstraint();
	}

	public void PropertySimpleAttributeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Attribute registration = vehicle.setAttribute("Registration");
		Generic myCar = vehicle.newInstance("myCar");
		Generic yourCar = vehicle.newInstance("yourCar");
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
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		final Relation driving = vehicle.setRelation("DrivingAlong", human);
		Generic myCar = vehicle.newInstance("myCar");
		Generic yourCar = vehicle.newInstance("yourCar");
		Generic myHuman = human.newInstance("myHuman");
		Generic yourHuman = human.newInstance("yourHuman");
		myCar.setLink(driving, "my_driving", myHuman);
		yourCar.setLink(driving, "your_driving", yourHuman);
		driving.enableUniqueValueConstraint();
	}

	public void PropertySimpleRelationKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine()
				.start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		final Relation driving = vehicle.setRelation("DrivingAlong", human);
		Generic myCar = vehicle.newInstance("myCar");
		Generic yourCar = vehicle.newInstance("yourCar");
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
