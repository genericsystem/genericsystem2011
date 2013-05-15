package org.genericsystem.web.util;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseListener {

	private static final Logger log = LoggerFactory.getLogger(PhaseListener.class);

	public void observeBefore(@Observes @Before PhaseEvent e) {
		// FacesContext facesContext = e.getFacesContext();
		// this.saveMessages(facesContext);
		//
		// if (PhaseId.RENDER_RESPONSE.equals(e.getPhaseId())) {
		// if (!facesContext.getResponseComplete()) {
		// this.restoreMessages(facesContext);
		// }
		// }
		log.info("\n\n\n---------------------------------" + e.getPhaseId().toString() + "-----------------------------------------\n");
	}

	public void observeAfter(@Observes @After PhaseEvent e) {
		log.info("\n----------------------------------------------------------------------------------------------------------------\n\n\n");
		// if (!PhaseId.RENDER_RESPONSE.equals(e.getPhaseId())) {
		// FacesContext facesContext = e.getFacesContext();
		// this.saveMessages(facesContext);
		// }
	}

	// @SuppressWarnings("unchecked")
	// private int saveMessages(final FacesContext facesContext) {
	// log.info("Save messages");
	// List<FacesMessage> messages = new ArrayList<FacesMessage>();
	// for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();) {
	// Object message = iter.next();
	// messages.add((FacesMessage) message);
	// iter.remove();
	// log.info("Save message : " + message);
	// }
	//
	// if (messages.size() == 0) {
	// return 0;
	// }
	//
	// Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
	// List<FacesMessage> existingMessages = (List<FacesMessage>) sessionMap.get(sessionToken);
	// if (existingMessages != null) {
	// existingMessages.addAll(messages);
	// } else {
	// sessionMap.put(sessionToken, messages);
	// }
	// return messages.size();
	// }
	//
	// @SuppressWarnings("unchecked")
	// private int restoreMessages(final FacesContext facesContext) {
	// log.info("Restore messages");
	// Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
	// List<FacesMessage> messages = (List<FacesMessage>) sessionMap.remove(sessionToken);
	//
	// if (messages == null) {
	// return 0;
	// }
	//
	// int restoredCount = messages.size();
	// for (Object element : messages) {
	// log.info("Restore message : " + element);
	// facesContext.addMessage(null, (FacesMessage) element);
	// }
	// return restoredCount;
	// }
}