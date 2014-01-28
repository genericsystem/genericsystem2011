package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericSystem;
import org.testng.annotations.Test;

@Test
public class ValueTest extends AbstractTest {

	public void testValueOfEngine() {
		Engine engine = GenericSystem.newCacheOnANewInMemoryEngine().getEngine();
		assert engine.getValue() != null;
	}

	public void testValueOfType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.getEngine().addSubType("Car").getValue();
			}
		}.assertIsCausedBy(UnsupportedOperationException.class);
	}
}
