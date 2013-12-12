package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.exception.SizeConstraintViolationException;
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

	public void requiredAddedAndRemovedKOByFlushing() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		final Attribute wheel = vehicle.setAttribute("wheel");
		final Holder wheelMyFiat = myVehicle.setValue(wheel, "4");
		wheel.enableRequiredConstraint();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				wheelMyFiat.remove();
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredAddedAndRemovedThenReadded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myFiat = vehicle.addInstance("myFiat");
		final Attribute wheel = vehicle.setAttribute("wheel");
		final Holder wheelMyFiat = myFiat.setValue(wheel, "4");
		wheel.enableRequiredConstraint();

		assert myFiat.getValues(wheel).get(0).equals("4");
		assert myFiat.getValues(wheel).size() == 1;

		wheelMyFiat.remove();

		assert !wheelMyFiat.isAlive();
		assert myFiat.getValues(wheel).isEmpty();

		myFiat.setValue(wheel, "6");

		assert myFiat.getValues(wheel).get(0).equals("6");
		assert myFiat.getValues(wheel).size() == 1;

		cache.flush();
	}

	public void addOneRequiredOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehicleWheel = vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		cache.flush();

		Generic myVehicle = vehicle.addInstance("myVehicle");

		myVehicle.setValue(vehicleWheel, "myFiatWheel");
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

	public void addOneSizeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.setAttribute("vehicleWheel").enableSizeConstraint(0, 1);
		cache.flush();
		vehicle.addInstance("myFiat");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void addRequiredOnSubTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("car");
		Attribute vehicleWheel = vehicle.setAttribute("vehicleWheel").enableRequiredConstraint();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myCar = car.addInstance("myCar");
		myVehicle.setValue(vehicleWheel, "myFiatWheel");
		myCar.setValue(vehicleWheel, "myFiatWheel");
		cache.flush();
	}

	public void addRequiredOnSubTypeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");

		final Attribute power = vehicle.setAttribute("power");
		power.enableRequiredConstraint();
		final Attribute power2 = vehicle.setAttribute("power");

		final Generic myCar = car.addInstance("myCar");
		myCar.setValue(power2, 123);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				Holder valeur = myCar.getHolder(power2);
				valeur.remove();
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addRequiredOnBaseButNotOnSubTypeOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type superCar = vehicle.addSubType("superCar");

		final Attribute power = vehicle.setAttribute("power");
		power.enableRequiredConstraint();
		final Attribute power2 = vehicle.setAttribute("power");
		power2.disableRequiredConstraint();

		final Generic myVehicle = car.addInstance("myVehicle");
		myVehicle.setValue(power, 123);
		final Generic myCar = car.addInstance("myCar");
		myCar.setValue(power2, 123);
		myCar.remove();
		final Generic mySuperCar = superCar.addInstance("mySuperCar");
		mySuperCar.setValue(power2, 123);
		mySuperCar.remove();

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
