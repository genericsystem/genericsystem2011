package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.EngineConsistencyConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class EngineConsistencyConstraintTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.setAttribute(GenericSystem.newCacheOnANewInMemoryEngine().start(), "Power");
			}
		}.assertIsCausedBy(EngineConsistencyConstraintViolationException.class);
	}

}
