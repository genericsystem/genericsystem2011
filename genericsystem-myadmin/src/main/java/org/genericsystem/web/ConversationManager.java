package org.genericsystem.web;

import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.weld.context.ManagedConversation;

@Named
public class ConversationManager {

	private static final String CONV_ATTR_NAME = org.jboss.weld.context.AbstractConversationContext.CONVERSATIONS_ATTRIBUTE_NAME;

	@Inject
	private Conversation conversation;

	@SuppressWarnings("unchecked")
	public void beginConversation() {

		if (conversation.isTransient()) {
			conversation.begin();

			HttpServletRequest request = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
			HttpSession session = request.getSession();
			Map<String, ManagedConversation> requestConversations = (Map<String, ManagedConversation>) request.getAttribute(CONV_ATTR_NAME);
			Map<String, ManagedConversation> sessionConversations = (Map<String, ManagedConversation>) session.getAttribute(CONV_ATTR_NAME);

			if (sessionConversations == null)
				session.setAttribute(CONV_ATTR_NAME, requestConversations);
			else
				sessionConversations.put(conversation.getId(), requestConversations.get(conversation.getId()));
		}
	}

	public String getId() {
		System.out.println("GETTING CID : " + conversation.getId());
		return conversation.getId();
	}

	public void go() {
		System.out.println("Going CID : " + conversation.getId());
		return;
	}
}
