package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.SingularInstanceConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class SingularInstanceConstraintTest extends AbstractTest {

	public void singleInstance() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		cache.newType("Human");
		car.enableSingularInstanceConstraint(cache);
		car.newInstance(cache,"myBmw");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.newInstance(cache,"myFerrari");
			}
		}.assertIsCausedBy(SingularInstanceConstraintViolationException.class);
	}

	public void imposingSingularityAfterTwoInstantiations() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		car.newInstance(cache,"myBmw");
		car.newInstance(cache,"myFerrari");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.enableSingularInstanceConstraint(cache);
			}
		}.assertIsCausedBy(SingularInstanceConstraintViolationException.class);
	}

	public void singularityCountSubtypeInstances() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache,"car");
		vehicle.enableSingularInstanceConstraint(cache);
		vehicle.newInstance(cache,"myTrafficTank");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.newInstance(cache,"mySmart");
			}
		}.assertIsCausedBy(SingularInstanceConstraintViolationException.class);
	}

}
