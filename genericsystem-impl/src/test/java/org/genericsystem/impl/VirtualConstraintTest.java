package org.genericsystem.impl;

import org.genericsystem.constraints.VirtualConstraintImpl;
import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.VirtualConstraintException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class VirtualConstraintTest extends AbstractTest {

	public void testVirtual() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.enableVirtualConstraint();
		Type car = vehicle.addSubType("Car");
		car.addInstance("myCar");
	}

	public void testVirtualWithJump() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.enableVirtualConstraint();
		Type car = vehicle.addSubType("Car");
		Type superCar = car.addSubType("SuperCar");
		assert !(cache.<GenericImpl> find(VirtualConstraintImpl.class)).isInheritanceEnabled();
		assert !superCar.isVirtualConstraintEnabled();
		superCar.addInstance("mySuperCar");
	}

	public void testVirtualKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		vehicle.enableVirtualConstraint();
		cache.flush();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.addInstance("myVehicle");
			}
		}.assertIsCausedBy(VirtualConstraintException.class);
		vehicle.disableVirtualConstraint();
		assert !vehicle.isVirtualConstraintEnabled();
		vehicle.addInstance("myVehicle");
	}

	public void testVirtualKO2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		vehicle.addInstance("myVehicle");
		cache.flush();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.enableVirtualConstraint();
			}
		}.assertIsCausedBy(VirtualConstraintException.class);

	}
}
