package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InheritanceTest extends AbstractTest {

	public void testComplexInheritanceIterator() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myBmw = vehicle.addInstance("myBmw");
		Type property = cache.addType("Property");
		Type power = property.addSubType("Power");
		Attribute vehiclePower = ((GenericImpl) vehicle).addSubAttribute((Attribute) power, "Power");
		assert vehiclePower.inheritsFrom(power);
		assert vehiclePower.inheritsFrom(property);
		myBmw.addValue(vehiclePower, 235);
		assert myBmw.getValue((Holder) vehiclePower).equals(235);
		assert myBmw.getValue((Holder) power).equals(235);
		assert myBmw.getValue((Holder) property).equals(235);
	}

	public void testComplexInheritanceIterator2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.addSubType("Vehicle");
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void testComplexInheritanceIterator3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Type car = vehicle.addSubType("Car");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.addSubType("Vehicle");
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	public void testComplexInheritanceIterator4() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.addAttribute("power");
		cache.addType("power");
	}
}
