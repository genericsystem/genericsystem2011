package org.genericsystem.test;

import org.testng.annotations.Test;

@Test
public class AllProvidersInjectionTest extends AbstractTest {
	
	public void testEngineInjection() {
		assert engine != null;
	}

	public void testCacheContextInjection() {
		assert context != null;
	}
}
