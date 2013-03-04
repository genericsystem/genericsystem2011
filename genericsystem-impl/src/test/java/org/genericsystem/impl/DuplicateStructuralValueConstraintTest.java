package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.DuplicateStructuralValueConstraintViolationException;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class DuplicateStructuralValueConstraintTest extends AbstractTest {

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
		}.assertIsCausedBy(DuplicateStructuralValueConstraintViolationException.class);
	}
}
