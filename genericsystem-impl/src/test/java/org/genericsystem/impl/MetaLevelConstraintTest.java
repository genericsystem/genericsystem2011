package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.MetaLevelConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MetaLevelConstraintTest extends AbstractTest {

	public void addAttributeOnInstanceFail() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("vehicle");
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				((Type) myVehicle).setAttribute("power");
			}
		}.assertIsCausedBy(MetaLevelConstraintViolationException.class);
	}
}
