package org.genericsystem.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.international.status.Message;
import org.jboss.seam.international.status.MessageFactory;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class RedirectMessages implements Serializable {

	private static final long serialVersionUID = 7666964394295933934L;
	private static final Logger log = LoggerFactory.getLogger(RedirectMessages.class);

	@Inject
	FacesContext context;

	@Inject
	Messages messages;

	@Inject
	MessageFactory factory;

	private List<Message> messagesToRedirect = new ArrayList<>();

	public void addErrorMessage(String key, Object... params) {
		messagesToRedirect.add(factory.error(new BundleKey("/bundles/messages", key), params).build());
		log.info("ZZZZZZZZZZZZZZZZZ");
	}

	public void restoreMessages(@Observes @After PhaseEvent e) {
		if (PhaseId.RESTORE_VIEW.equals(e.getPhaseId())) {
			for (Message message : messagesToRedirect)
				messages.add(message);
			messagesToRedirect.clear();
		}
	}
}
