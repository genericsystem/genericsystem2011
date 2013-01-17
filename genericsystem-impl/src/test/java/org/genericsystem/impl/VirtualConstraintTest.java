package org.genericsystem.impl;

import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.VirtualConstraint;
import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.VirtualConstraintException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class VirtualConstraintTest extends AbstractTest {

	public void virtualConstraintSimple() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint(cache);
		assert vehicle.isVirtualConstraintEnabled(cache);
	}

	public void virtualConstraintOnType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint(cache);
		assert vehicle.isVirtualConstraintEnabled(cache);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.newInstance(cache, "myAudi");
			}
		}.assertIsCausedBy(VirtualConstraintException.class);
	}

	public void virtualConstraintOnSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.enableVirtualConstraint(cache);
		assert vehicle.isVirtualConstraintEnabled(cache);
		Type car = vehicle.newSubType(cache, "car");
		assert !car.isVirtualConstraintEnabled(cache);
		car.newInstance(cache, "myAudi");
		cache.flush();
	}

	public void virtualConstraintOnAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "car");
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		Generic myAudi = car.newInstance(cache, "myAudi");
		vehiclePower.enableVirtualConstraint(cache);
		assert vehiclePower.isVirtualConstraintEnabled(cache);
		Attribute horsePower = car.addSubAttribute(cache, vehiclePower, "horsePower");
		myAudi.addValue(cache, horsePower, "200");
		cache.flush();
	}

	@SystemGeneric
	@VirtualConstraint
	public static class MyVirtualType {
	}

	public void testVirtualStaticLoading() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyVirtualType.class);
		final Type myVirtualType = cache.find(MyVirtualType.class);
		assert myVirtualType.isVirtualConstraintEnabled(cache);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVirtualType.newAnonymousInstance(cache);

			}
		}.assertIsCausedBy(VirtualConstraintException.class);
	}
}
