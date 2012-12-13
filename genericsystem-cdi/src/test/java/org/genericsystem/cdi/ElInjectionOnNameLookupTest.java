package org.genericsystem.cdi;

import org.testng.annotations.Test;

@Test
public class ElInjectionOnNameLookupTest extends AbstractTest {

	public void testStringInjectionOnNameLookup() {
		assert "coucou".equals(expressions.evaluateValueExpression("#{'coucou'}"));
	}

	public void testEngineInjectionOnNameLookup() {
		assert expressions.evaluateValueExpression("#{engine}") != null;
	}

	public void testCacheContextInjectionOnNameLookup() {
		assert expressions.evaluateValueExpression("#{cache}") != null;
	}
}
