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
public class ConsistencySizeConstraintTest extends AbstractTest {

	public void consistencyAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		power.newInstance(123, myVehicle); // or myVehicle.setValue(power, 123);
		power.enableSizeConstraint(Statics.BASE_POSITION, 1);
	}

	public void consistencyAttributeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		final Attribute power = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		power.newInstance(123, myVehicle);
		power.newInstance(150, myVehicle);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				power.enableSizeConstraint(Statics.BASE_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationBaseOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.newInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
	}

	public void consistencyRelationBaseKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		Generic green = color.newInstance("green");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.newInstance("myVehicleRed", myVehicle, red);
		vehicleColor.newInstance("myVehicleGreen", myVehicle, green);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationTargetOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.newInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
	}

	public void consistencyRelationTargetKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		final Generic red = color.newInstance("red");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.newInstance("myVehicleRed", myVehicle, red);
		vehicleColor.newInstance("myVehicleGreen", myVehicle, red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationDoubleOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.newInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
	}

}
