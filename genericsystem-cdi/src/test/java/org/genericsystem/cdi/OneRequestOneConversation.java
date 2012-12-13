package org.genericsystem.cdi;

import org.testng.annotations.Test;

@Test
public class OneRequestOneConversation extends AbstractTest {

	private String idContext;

	public void testA() {
		idContext = cache.toString();
	}

	public void testB() {
		assert idContext.equals(cache.toString());
	}
}
