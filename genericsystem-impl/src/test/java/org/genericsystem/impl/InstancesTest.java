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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType = cache.addType("newType");
		Generic aNewType = newType.newInstance( "aNewType");
		assert newType.getInstances().size() == 1;
		assert newType.getInstances().contains(aNewType);
	}

	public void testSubTypeInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType = cache.addType("newType");
		Type newSubType = newType.newSubType( "newSubType");
		Generic aNewType = newType.newInstance( "aNewType");
		Generic aNewSubType = newSubType.newInstance( "aNewSubType");
		assert newType.getInstances().size() == 1;
		assert newType.getInstances().contains(aNewType);
		assert newSubType.getInstances().size() == 1;
		assert newSubType.getInstances().contains(aNewSubType);
	}

	public void testAttributeInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType = cache.addType("newType");
		Attribute newAttribute = cache.getEngine().setAttribute( "newAttribute");
		Generic aNewType1 = newType.newInstance( "aNewType1");
		Holder value1 = aNewType1.setValue( newAttribute, "value1");
		assert newAttribute.getInstances().size() == 1;
		assert newAttribute.getInstances().contains(value1);
		Generic aNewType2 = newType.newInstance( "aNewType2");
		Holder value2 = aNewType2.setValue( newAttribute, "value2");
		assert value1.isAlive();
		assert newAttribute.getInstances().size() == 2;
		assert newAttribute.getInstances().contains(value1);
		assert newAttribute.getInstances().contains(value2);
	}

}
