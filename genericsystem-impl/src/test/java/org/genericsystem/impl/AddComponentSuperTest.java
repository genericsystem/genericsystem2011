package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AddComponentSuperTest extends AbstractTest {

	public void addAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type power = cache.newType("power");
		log.info("metaLevel power " + power.getMetaLevel());
		assert vehicle.getAttribute(cache, "power") == null;
		power.addComponent(cache, 0, vehicle);
		assert vehicle.getAttribute(cache, "power").equals(power);
	}

}
