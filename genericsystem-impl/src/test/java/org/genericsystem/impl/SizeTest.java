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
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		assert vehicleColor.getSizeConstraint(Statics.BASE_POSITION) == 1 : vehicleColor.getSizeConstraint(Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(Statics.BASE_POSITION);
	}

	public void enableSizeInTargetPosition() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
		assert vehicleColor.getSizeConstraint(Statics.TARGET_POSITION) == 1 : vehicleColor.getSizeConstraint(Statics.TARGET_POSITION);
		vehicleColor.disableSizeConstraint(Statics.TARGET_POSITION);
	}

	public void enableSizeTwoAxe() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
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
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
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
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		vehiclePower.addInstance(123, myVehicle);
		vehiclePower.addInstance(126, myVehicle);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(vehiclePower, 123);
		myVehicle.setValue(vehiclePower, 126);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		Generic myCar = car.addInstance("myCar1");
		myCar.setValue(vehiclePower, 123);
		myCar.setValue(vehiclePower, 126);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");
		vehiclePower.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicle.setValue(vehiclePower, 123);
		car.setValue(vehiclePower, 123);
	}

	public void checkConstraintWithRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		Generic myVehicle1 = vehicle.addInstance("myVehicle1");
		Generic red = color.addInstance("red");
		vehicleColor.addInstance("link1", myVehicle1, red);
		vehicleColor.addInstance("link2", myVehicle1, red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}
}
