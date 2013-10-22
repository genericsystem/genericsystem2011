package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.SizeConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class SizeTest extends AbstractTest {

	public void enableSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) == 1 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(Statics.BASE_POSITION);
	}

	public void enableSizeInTargetPosition() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
		assert vehicleColor.getSizeConstraint(Statics.TARGET_POSITION) == 1 : vehicleColor.getSizeConstraint(Statics.TARGET_POSITION);
		vehicleColor.disableSizeConstraint(Statics.TARGET_POSITION);
	}

	public void enableSizeTwoAxe() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 2);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) != 1 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) == 2 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) == null : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
	}

	public void enableComplexSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 2);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) == 1 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(Statics.TARGET_POSITION) == 2 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(Statics.TARGET_POSITION) == 2 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
	}

	public void checkConstraintWithAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		final Generic myVehicle = vehicle.newInstance("myVehicle");
		vehiclePower.newInstance(123, myVehicle);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehiclePower.newInstance(126, myVehicle);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		final Generic myVehicle1 = vehicle.newInstance("myVehicle1");
		myVehicle1.setValue(vehiclePower, 123);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle1.setValue(vehiclePower, 126);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType("Car");
		final Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		final Generic myCar1 = car.newInstance("myCar1");
		myCar1.setValue(vehiclePower, 123);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar1.setValue(vehiclePower, 126);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicle.setValue(vehiclePower, 123);
		car.setValue(vehiclePower, 123);

	}

	public void checkConstraintWithRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		final Generic myVehicle1 = vehicle.newInstance("myVehicle1");
		final Generic red = color.newInstance("red");
		vehicleColor.newInstance("link1", myVehicle1, red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicleColor.newInstance("link2", myVehicle1, red);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}
}
