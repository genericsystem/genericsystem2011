package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest extends AbstractTest {

	public void requiredAddedAndRemoved() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheel = vehicle.setAttribute(cache, "wheel");
		wheel.enableRequiredConstraint(cache);
		assert wheel.isRequiredConstraintEnabled(cache);
		Holder wheelMyFiat = myFiat.setValue(cache, wheel, "BigWheel");
		cache.flush();
		wheelMyFiat.remove(cache);
		assert !wheelMyFiat.isAlive(cache);
		assert wheel.isRequiredConstraintEnabled(cache);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredNeverAdded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance(cache, "myFiat");
		vehicle.setAttribute(cache, "wheel").enableRequiredConstraint(cache);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addOneRequired() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute vehicleWheel = vehicle.setAttribute(cache, "wheel").enableRequiredConstraint(cache);
		myFiat.setValue(cache, vehicleWheel, "BigWheel");
		cache.flush();
	}

	// public void addSubOneRequired() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicleType = cache.newType("Vehicle");
	// Generic myFiat = vehicleType.newInstance(cache, "myFiat");
	// Attribute wheel = vehicleType.addAttribute(cache, "wheel");
	// wheel.enableRequiredConstraint(cache);
	// Attribute subAttribute = vehicleType.addSubAttribute(cache, wheel, "LittleWheel");
	// myFiat.setValue(cache, subAttribute, "littlePinkWheel");
	// cache.flush();
	// }

	public void addRequiredOnSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		vehicleType.setAttribute(cache, "wheel").enableRequiredConstraint(cache);
		Type carType = vehicleType.newSubType(cache, "Car");
		carType.newInstance(cache, "myFiat");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableRequiredConstraint(cache, Statics.BASE_POSITION);
		cache.flush();
		assert carColor.isRequiredConstraintEnabled(cache, Statics.BASE_POSITION);
		car.newInstance(cache, "myFiat");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationTargetSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableRequiredConstraint(cache, Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled(cache, Statics.TARGET_POSITION);
		color.newInstance(cache, "red");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSideOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableRequiredConstraint(cache, Statics.BASE_POSITION);
		assert carColor.isRequiredConstraintEnabled(cache, Statics.BASE_POSITION);
		color.newInstance(cache, "red");
		cache.flush();
	}

	public void addRequiredOnRelationOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableRequiredConstraint(cache, Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled(cache, Statics.TARGET_POSITION);
		car.newInstance(cache, "myFiat");
		cache.flush();
	}
}
