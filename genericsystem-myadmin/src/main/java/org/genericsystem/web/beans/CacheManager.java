package org.genericsystem.web.beans;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.web.qualifiers.DiscardEvent;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class CacheManager implements Serializable {

	private static final long serialVersionUID = 3799172132114867212L;

	@Inject
	private Logger log;

	@Inject
	private Conversation conversation;

	@Inject
	@DiscardEvent
	private Event<Generic> discardEvent;

	@Inject
	private TypesManager typesManager;

	@Inject
	private Cache cacheContext;

	public void discard() {
		if (typesManager.getSelectedType() != null)
			discardEvent.fire(typesManager.getSelectedType());
		conversation.end();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/gsmyadmin/pages/index.xhtml");
		} catch (IOException e) {
			log.info(e.getMessage(), e.getCause());
		}
	}

	public void save() {
		this.cacheContext.flush();
	}

}
