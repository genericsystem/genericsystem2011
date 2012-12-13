package org.genericsystem.test;

import org.testng.annotations.Test;

public class ElInjectionOnNameLookupTest extends AbstractTest {

	@Test
	public void testStringInjectionOnNameLookup() {
		assert "coucou".equals(expressions.evaluateValueExpression("#{'coucou'}"));
	}

	@Test
	public void testEngineInjectionOnNameLookup() {
		assert expressions.evaluateValueExpression("#{engine}") != null;
	}

	@Test
	public void testCacheContextInjectionOnNameLookup() {
		assert expressions.evaluateValueExpression("#{context}") != null;
	}
}
