package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
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
}
