package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.SingletonConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConsistencySingletonConstraintTest extends AbstractTest {

	public void consistencyTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		log.info("zegfkugeukgekuf");
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance("myVehicle");
		vehicle.enableSingletonConstraint();
	}

	public void consistencyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance("myVehicle");
		vehicle.newInstance("myVehicle2");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.enableSingletonConstraint();
			}
		}.assertIsCausedBy(SingletonConstraintViolationException.class);
	}
}
