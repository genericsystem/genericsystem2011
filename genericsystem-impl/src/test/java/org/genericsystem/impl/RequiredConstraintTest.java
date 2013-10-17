package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest extends AbstractTest {

	public void requiredAddedAndRemoved() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance("myFiat");
		final Attribute wheel = vehicle.setAttribute("wheel");
		Holder wheelMyFiat = myFiat.setValue(wheel, "4");
		cache.flush();
		wheelMyFiat.remove();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				wheel.enableRequiredConstraint();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredNeverAdded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance("myFiat");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.setAttribute("wheel").enableRequiredConstraint();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addOneRequired() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehicleWheel = vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		Generic myFiat = vehicle.newInstance("myFiat");
		myFiat.setValue(vehicleWheel, "myFiatWheel");
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
		Type vehicle = cache.newType("Vehicle");
		vehicle.setAttribute("power").enableRequiredConstraint();
		Type car = vehicle.newSubType("Car");
		car.newInstance("myFiat");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSideEmpty() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		color.newInstance("red");
		cache.flush();
	}

	public void addRequiredOnRelationTargetSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled(Statics.TARGET_POSITION);
		color.newInstance("red");

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
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		car.newInstance("myFiat").bind(carColor, color);
		cache.flush();
	}

	public void addRequiredOnRelationBaseSideOk2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		Generic myFiat = car.newInstance("myFiat");
		Generic red = color.newInstance("red");
		Link myFiatRed = myFiat.setLink(carColor, "myFiatRed", red);
		cache.flush();
		myFiatRed.remove();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.TARGET_POSITION);
		car.bind(carColor, color.newInstance("red"));
		cache.flush();
	}

}
