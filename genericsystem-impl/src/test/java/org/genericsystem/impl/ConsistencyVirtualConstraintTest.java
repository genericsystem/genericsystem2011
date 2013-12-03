package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.VirtualConstraintException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConsistencyVirtualConstraintTest extends AbstractTest {

	public void consistencyTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.enableVirtualConstraint();
	}
	
	public void consistencyTypeOK2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		car.addAnonymousInstance();
		vehicle.enableVirtualConstraint();
	}

	public void consistencyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		vehicle.addInstance("myVehicle");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.enableVirtualConstraint();
			}
		}.assertIsCausedBy(VirtualConstraintException.class);
	}
}
