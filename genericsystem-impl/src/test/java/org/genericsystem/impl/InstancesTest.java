package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InstancesTest extends AbstractTest {

	public void testTypeInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType = cache.newType("newType");
		Generic aNewType = newType.newInstance(cache, "aNewType");
		assert newType.getInstances(cache).size() == 1;
		assert newType.getInstances(cache).contains(aNewType);
	}

	public void testSubTypeInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType = cache.newType("newType");
		Type newSubType = newType.newSubType(cache, "newSubType");
		Generic aNewType = newType.newInstance(cache, "aNewType");
		Generic aNewSubType = newSubType.newInstance(cache, "aNewSubType");
		assert newType.getInstances(cache).size() == 1;
		assert newType.getInstances(cache).contains(aNewType);
		assert newSubType.getInstances(cache).size() == 1;
		assert newSubType.getInstances(cache).contains(aNewSubType);
	}

	public void testAttributeInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType = cache.newType("newType");
		Attribute newAttribute = cache.getEngine().setAttribute(cache, "newAttribute");
		Generic aNewType1 = newType.newInstance(cache, "aNewType1");
		Holder value1 = aNewType1.setValue(cache, newAttribute, "value1");
		assert newAttribute.getInstances(cache).size() == 1;
		assert newAttribute.getInstances(cache).contains(value1);
		Generic aNewType2 = newType.newInstance(cache, "aNewType2");
		Holder value2 = aNewType2.setValue(cache, newAttribute, "value2");
		assert value1.isAlive(cache);
		assert newAttribute.getInstances(cache).size() == 2;
		assert newAttribute.getInstances(cache).contains(value1);
		assert newAttribute.getInstances(cache).contains(value2);
	}

}
