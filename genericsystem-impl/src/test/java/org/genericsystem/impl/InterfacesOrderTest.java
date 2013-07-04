package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InterfacesOrderTest extends AbstractTest {

	public void testOrderInterfaces() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type T1 = cache.newType("T1");
		Type T2 = cache.newType("T2");
		Type T3 = cache.newSubType("T3", T1, T2);

		Type T4 = T1.newSubType( "T4");
		Type T5 = T2.newSubType( "T5");
		Type T6 = cache.newSubType("T6", T4, T5, T3);

		assert T6.inheritsFrom(T3);
	}
}
