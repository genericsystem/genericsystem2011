package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.SizeConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.testng.annotations.Test;

@Test
public class SizeTest extends AbstractTest {

	public void mountSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type sizeConstraint = cache.find(SizeConstraintImpl.class);
		assert sizeConstraint != null;
		assert sizeConstraint.getAttribute(cache, SizeConstraintImpl.SIZE) != null : sizeConstraint.info() + " " + sizeConstraint.getAttributes(cache);
	}

	public void enableSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		assert vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION) == 1 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(cache, Statics.BASE_POSITION);
	}

	public void enableSizeInTargetPosition() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enableSizeConstraint(cache, Statics.TARGET_POSITION, 1);
		assert vehicleColor.getSizeConstraint(cache, Statics.TARGET_POSITION) == 1 : vehicleColor.getSizeConstraint(cache, Statics.TARGET_POSITION);
		vehicleColor.disableSizeConstraint(cache, Statics.TARGET_POSITION);
	}

	public void enableSizeTwoAxe() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(cache, Statics.BASE_POSITION, 2);
		assert vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION) != 1 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION) == 2 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(cache, Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION) == null : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
	}

	public void enableComplexSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(cache, Statics.TARGET_POSITION, 2);
		assert vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION) == 1 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(cache, Statics.TARGET_POSITION) == 2 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
		vehicleColor.disableSizeConstraint(cache, Statics.BASE_POSITION);
		assert vehicleColor.getSizeConstraint(cache, Statics.TARGET_POSITION) == 2 : vehicleColor.getSizeConstraint(cache, Statics.BASE_POSITION);
	}

	public void checkConstraintWithAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		vehiclePower.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		final Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		vehiclePower.newInstance(cache, 123, myVehicle1);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehiclePower.newInstance(cache, 126, myVehicle1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		vehiclePower.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		final Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		myVehicle1.setValue(cache, vehiclePower, 123);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle1.setValue(cache, vehiclePower, 126);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache, "Car");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		vehiclePower.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		final Generic myCar1 = car.newInstance(cache, "myCar1");
		myCar1.setValue(cache, vehiclePower, 123);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar1.setValue(cache, vehiclePower, 126);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void checkConstraintWithAttributeWithInherits2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache, "Car");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		vehiclePower.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		car.newInstance(cache, "myCar1").setValue(cache, vehiclePower, 123);
		vehicle.newInstance(cache, "myVehicle1").setValue(cache, vehiclePower, 123);
	}

	public void checkConstraintWithRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		final Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		final Generic red = color.newInstance(cache, "red");
		vehicleColor.newInstance(cache, "link1", myVehicle1, red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicleColor.newInstance(cache, "link2", myVehicle1, red);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}
}
