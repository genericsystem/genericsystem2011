package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.VirtualConstraintException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class VirtuakConstraintTest extends AbstractTest {

	public void testVirtual() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint();
		Type car = vehicle.newSubType("Car");
		car.newInstance("myCar");
	}

	public void testVirtualWithJump() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint();
		Type car = vehicle.newSubType("Car");
		Type superCar = car.newSubType("SuperCar");
		superCar.newInstance("mySuperCar");
	}

	public void testVirtualKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint();
		cache.flush();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.newInstance("myVehicle");
			}
		}.assertIsCausedBy(VirtualConstraintException.class);
		vehicle.disableVirtualConstraint();
		vehicle.newInstance("myVehicle");
	}

	public void testVirtualKO2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance("myVehicle");
		cache.flush();
		vehicle.enableVirtualConstraint();
	}
}
