package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class EngineConsistencyConstraintTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		try {
			((EngineImpl) vehicle.getEngine()).start(GenericSystem.newCacheOnANewInMemoryEngine());
		} catch (IllegalStateException ignore) {

		} catch (Exception e) {
			assert false;
		}
	}
}
