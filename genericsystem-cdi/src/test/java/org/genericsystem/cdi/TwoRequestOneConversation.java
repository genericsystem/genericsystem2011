package org.genericsystem.cdi;

import org.testng.annotations.Test;

@Test
public class TwoRequestOneConversation extends AbstractTest {

	private String idConversation;

	private String idContext;

	public void testA() {
		conversation.begin();
		idConversation = conversation.getId();
		idContext = cache.toString();
	}

	public void testB() {
		assert idConversation.equals(conversation.getId());
		assert idContext.equals(cache.toString());
		conversation.end();
	}
}
