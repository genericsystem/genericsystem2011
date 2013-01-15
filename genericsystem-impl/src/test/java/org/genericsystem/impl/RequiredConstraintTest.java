package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest extends AbstractTest {

	public void requiredAddedAndRemoved() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheel = vehicle.addAttribute(cache, "wheel");
		wheel.enableRequiredConstraint(cache);
		Value wheelMyFiat = myFiat.addValue(cache, wheel, "BigWheel");
		cache.flush();
		wheelMyFiat.remove(cache);

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
		vehicle.addAttribute(cache, "wheel").enableRequiredConstraint(cache);
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
		Attribute vehicleWheel = vehicle.addAttribute(cache, "wheel").enableRequiredConstraint(cache);
		myFiat.addValue(cache, vehicleWheel, "BigWheel");
		cache.flush();
	}

	public void addSubOneRequired() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		Generic myFiat = vehicleType.newInstance(cache, "myFiat");
		Attribute wheel = vehicleType.addAttribute(cache, "wheel");
		wheel.enableRequiredConstraint(cache);
		Attribute subAttribute = vehicleType.addSubAttribute(cache, wheel, "LittleWheel");
		myFiat.addValue(cache, subAttribute, "littlePinkWheel");
		cache.flush();
	}

	public void addRequiredOnSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		vehicleType.addAttribute(cache, "wheel").enableRequiredConstraint(cache);
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
		Relation carColor = car.addRelation(cache, "carColor", car, color);
		carColor.enableRequiredConstraint(cache, Statics.BASE_POSITION);
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
		Relation carColor = car.addRelation(cache, "carColor", car, color);
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

}
