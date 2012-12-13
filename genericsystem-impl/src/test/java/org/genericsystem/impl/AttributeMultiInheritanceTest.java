package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class AttributeMultiInheritanceTest extends AbstractTest {

	public void simpleTest() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type classConstraintType = cache.newType("PropertyConstraintType");
		classConstraintType.setConstraintClass(cache, Integer.class);
		Type car = cache.newType("Car");
		final Attribute carPower = car.addSubAttribute(cache, (Attribute) classConstraintType, "Power");
		assert carPower.inheritsFrom(classConstraintType);
		assert Integer.class.equals(carPower.getConstraintClass(cache));

		final Generic myBmw = car.newInstance(cache, "myBmw");

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.addValue(cache, carPower, "200");
			}
		}.assertIsCausedBy(ClassInstanceConstraintViolationException.class);
	}
}
