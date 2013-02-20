package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.DuplicateNameRelationConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class DuplicateNameRelationConstraintTest extends AbstractTest {

	public void testKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		final Type color = cache.newType("Color");
		car.setAttribute(cache, "carColor");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.setRelation(cache, "carColor", color);
			}
		}.assertIsCausedBy(DuplicateNameRelationConstraintViolationException.class);
	}
}
