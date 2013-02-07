package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class EngineConsistencyConstraintTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.setAttribute(GenericSystem.newCacheOnANewInMemoryEngine(), "Power");
		// zzzzzzzz
	}

}
