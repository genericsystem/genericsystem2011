package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
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

	public void testContraint() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		((GenericImpl) cache.getEngine()).enableRequiredConstraint(0);
	}

	public void requiredAddedAndRemoved() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myFiat = vehicle.addInstance("myFiat");
		final Attribute wheel = vehicle.setAttribute("wheel");
		final Holder wheelMyFiat = myFiat.setValue(wheel, "4");
		wheel.enableRequiredConstraint();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				wheelMyFiat.remove();
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredNeverAdded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		Attribute wheel = vehicle.setAttribute("wheel");
		wheel.enableRequiredConstraint();
		cache.flush();

		vehicle.addInstance("myFiat");

		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addOneRequired() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehicleWheel = vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		cache.flush();

		Generic myFiat = vehicle.addInstance("myFiat");

		myFiat.setValue(vehicleWheel, "myFiatWheel");
		cache.flush();
	}

	public void addOneRequiredKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		cache.flush();
		vehicle.addInstance("myFiat");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredHeritageTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("car");
		Attribute vehicleWheel = vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		Generic myFiat = vehicle.addInstance("myFiat");
		Generic myCar = car.addInstance("myCar");
		myFiat.setValue(vehicleWheel, "myFiatWheel");
		myCar.setValue(vehicleWheel, "myFiatWheel");
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
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.setAttribute("power").enableRequiredConstraint();
		Type car = vehicle.addSubType("Car");
		car.addInstance("myFiat").setValue(power, 123);
		cache.flush();
	}

	public void addRequiredOnRelationBaseSideEmpty() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		car.addInstance("myCar");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationTargetSide() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.TARGET_POSITION);
		assert carColor.isRequiredConstraintEnabled(Statics.TARGET_POSITION);
		color.addInstance("red");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationBaseSideOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		car.addInstance("myFiat").bind(carColor, color);
		cache.flush();
	}

	public void testRemoveRequiredOnRelationBaseSideKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.BASE_POSITION);
		Generic red = color.addInstance("red");
		Generic myFiat = car.addInstance("myFiat");
		final Link myFiatRed = myFiat.setLink(carColor, "myFiatRed", red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myFiatRed.remove();
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnRelationOk() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableRequiredConstraint(Statics.TARGET_POSITION);
		car.bind(carColor, color.addInstance("red"));
		cache.flush();
	}

}
