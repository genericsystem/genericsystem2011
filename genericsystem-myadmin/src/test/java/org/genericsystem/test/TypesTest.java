package org.genericsystem.test;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class TypesTest extends AbstractTest {

	public void testTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		directSubTypes(cache, cache.getEngine());
	}

	private void directSubTypes(Cache cache, Type parent) {
		log.info("DirectSubTypes " + parent.toString());
		for (Type childen : parent.<Type> getDirectSubTypes(cache))
			directSubTypes(cache, childen);
	}

}
