package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InterfacesOrderTest extends AbstractTest {

	public void testOrderInterfaces() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type T1 = cache.newType("T1");
		Type T2 = cache.newType("T2");
		Type T3 = cache.newSubType("T3", T1, T2);
		T3.log();
		Type T4 = T1.newSubType("T4");
		T4.log();
		Type T5 = T2.newSubType("T5");
		Statics.debugCurrentThread();
		Type T6 = cache.newSubType("T6", T4, T5, T3);
		T6.log();
		assert T6.inheritsFrom(T3);
	}
}
