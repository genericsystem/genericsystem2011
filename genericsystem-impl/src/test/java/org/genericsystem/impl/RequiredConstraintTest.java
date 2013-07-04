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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance( "myFiat");
		Attribute wheel = vehicle.setAttribute( "wheel");
		wheel.enableRequiredConstraint();
		assert wheel.isRequiredConstraintEnabled();
		Holder wheelMyFiat = myFiat.setValue( wheel, "BigWheel");
		cache.flush();
		wheelMyFiat.remove();
		assert !wheelMyFiat.isAlive();
		assert wheel.isRequiredConstraintEnabled();

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredNeverAdded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance( "myFiat");
		vehicle.setAttribute( "wheel").enableRequiredConstraint();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addOneRequired() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance( "myFiat");
		Attribute vehicleWheel = vehicle.setAttribute( "wheel").enableRequiredConstraint();
		myFiat.setValue( vehicleWheel, "BigWheel");
		cache.flush();
	}

	// public void addSubOneRequired() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicleType = cache.newType("Vehicle");
	// Generic myFiat = vehicleType.newInstance( "myFiat");
	// Attribute wheel = vehicleType.addAttribute( "wheel");
	// wheel.enableRequiredConstraint();
	// Attribute subAttribute = vehicleType.addSubAttribute( wheel, "LittleWheel");
	// myFiat.setValue( subAttribute, "littlePinkWheel");
	// cache.flush();
	// }

	public void addRequiredOnSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicleType = cache.newType("Vehicle");
		vehicleType.setAttribute( "wheel").enableRequiredConstraint();
		Type carType = vehicleType.newSubType( "Car");
		carType.newInstance( "myFiat");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation( "carColor", color);
		carColor.enableRequiredConstraint( Statics.BASE_POSITION);
		cache.flush();
		assert carColor.isRequiredConstraintEnabled( Statics.BASE_POSITION);
		car.newInstance( "myFiat");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationTargetSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation( "carColor", color);
		carColor.enableRequiredConstraint( Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled( Statics.TARGET_POSITION);
		color.newInstance( "red");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSideOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation( "carColor", color);
		carColor.enableRequiredConstraint( Statics.BASE_POSITION);
		assert carColor.isRequiredConstraintEnabled( Statics.BASE_POSITION);
		color.newInstance( "red");
		cache.flush();
	}

	public void addRequiredOnRelationOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation( "carColor", color);
		carColor.enableRequiredConstraint( Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled( Statics.TARGET_POSITION);
		car.newInstance( "myFiat");
		cache.flush();
	}
}
