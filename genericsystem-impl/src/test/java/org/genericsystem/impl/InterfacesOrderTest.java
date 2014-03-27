package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InterfacesOrderTest extends AbstractTest {

	public void testOrderInterfaces() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type T1 = cache.addType("T1");
		Type T2 = cache.addType("T2");
		Type T3 = T1.addSubType("T3", new Generic[] { T2 });
		Type T4 = T1.addSubType("T4");
		Type T5 = T2.addSubType("T5");
		Type T6 = T4.addSubType("T6", new Generic[] { T5, T3 });
		assert T6.inheritsFrom(T3);
	}
}
