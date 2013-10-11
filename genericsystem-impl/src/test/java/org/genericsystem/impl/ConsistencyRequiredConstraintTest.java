package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConsistencyRequiredConstraintTest extends AbstractTest {

	public void ConsistencyAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Attribute power = vehicle.setAttribute("power");
		myVehicle.setValue(power, 123);
		power.enableRequiredConstraint();
		cache.flush();
	}

	public void ConsistencyAttributeKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine()
				.start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance("myVehicle");
		final Attribute power = vehicle.setAttribute("power");
		
		new RollbackCatcher() {
			@Override
			public void intercept() {		
				power.enableRequiredConstraint();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
		cache.flush();
	}

}
