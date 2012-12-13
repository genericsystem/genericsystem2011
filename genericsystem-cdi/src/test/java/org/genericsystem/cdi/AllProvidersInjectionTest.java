package org.genericsystem.cdi;

import org.testng.annotations.Test;

@Test
public class AllProvidersInjectionTest extends AbstractTest {

	public void testEngineInjection() {
		assert engine != null;
	}

	public void testCacheContextInjection() {
		assert cache != null;
	}
}
