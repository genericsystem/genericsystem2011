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
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		power.addInstance(123, myVehicle); // or myVehicle.setValue(power, 123);
		power.enableSizeConstraint(Statics.BASE_POSITION, 1);
	}

	public void consistencyAttributeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Attribute power = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		power.addInstance(123, myVehicle);
		power.addInstance(150, myVehicle);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				power.enableSizeConstraint(Statics.BASE_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationBaseOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.addInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
	}

	public void consistencyRelationBaseKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.addInstance("myVehicleRed", myVehicle, red);
		vehicleColor.addInstance("myVehicleGreen", myVehicle, green);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationTargetOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.addInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
	}

	public void consistencyRelationTargetKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		final Type color = cache.addType("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic red = color.addInstance("red");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.addInstance("myVehicleRed", myVehicle, red);
		vehicleColor.addInstance("myVehicleGreen", myVehicle, red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
			}
		}.assertIsCausedBy(SizeConstraintViolationException.class);
	}

	public void consistencyRelationDoubleOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.addInstance("myVehicleRed", myVehicle, red);
		vehicleColor.enableSizeConstraint(Statics.BASE_POSITION, 1);
		vehicleColor.enableSizeConstraint(Statics.TARGET_POSITION, 1);
	}

}
