package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConsistencySingularConstraintTest extends AbstractTest {

	public void consistencyRelationBaseOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		myVehicle2.setLink(vehicleColor, "myVehicle2Red", red);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
	}

	public void consistencyRelationBaseKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		myVehicle.setLink(vehicleColor, "myVehicleGreen", green);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

	}

	public void consistencyRelationTargetOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		myVehicle.setLink(vehicleColor, "myVehicleGreen", green);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);
	}

	public void consistencyRelationTargetKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		final Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		myVehicle2.setLink(vehicleColor, "myVehicleRed", red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

	}

	public void consistencyRelationDoubleOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
	}

	public void consistencyAttributeBaseOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.setInstance("myVehicle");
		Generic myVehicle2 = vehicle.setInstance("myVehicle");
		Attribute power = vehicle.setAttribute("power");
		myVehicle.setValue(power, 123);
		myVehicle2.setValue(power, 123);
		power.enableSingularConstraint(Statics.BASE_POSITION);
	}

	public void consistencyAttributeBaseKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		final Attribute power = vehicle.setAttribute("power");
		myVehicle.setValue(power, 123);
		myVehicle.setValue(power, 236);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				power.enableSingularConstraint(Statics.BASE_POSITION);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}
}
