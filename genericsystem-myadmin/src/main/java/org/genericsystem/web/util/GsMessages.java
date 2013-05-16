package org.genericsystem.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import org.jboss.seam.faces.event.qualifier.Before;
import org.jboss.seam.faces.event.qualifier.RenderResponse;
import org.jboss.seam.international.status.Message;
import org.jboss.seam.international.status.MessageFactory;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class GsMessages implements Serializable {

	private static final long serialVersionUID = 7666964394295933934L;
	protected static Logger log = LoggerFactory.getLogger(GsMessages.class);

	@Inject
	FacesContext context;

	@Inject
	Messages messages;

	@Inject
	MessageFactory factory;

	private static final String MESSAGES_BUNDLE_NAME = "/bundles/messages";

	private static BundleKey bundleKey(String key) {
		return new BundleKey(MESSAGES_BUNDLE_NAME, key);
	}

	private List<Message> messagesToRedirect = new ArrayList<>();

	public void restoreMessages(@Observes @RenderResponse @Before PhaseEvent e) {
		for (Message message : messagesToRedirect)
			messages.add(message);
		messagesToRedirect.clear();
	}

	public void redirectError(String key, Object... params) {
		messagesToRedirect.add(factory.error(bundleKey(key), params).build());
	}

	public void redirectStringError(String message, Object... params) {
		messagesToRedirect.add(factory.error(message, params).build());
	}

	public void info(String key, Object... params) {
		messages.info(bundleKey(key), params);
	}

	public void warn(String key, Object... params) {
		messages.warn(bundleKey(key), params);
	}

	public void error(String key, Object... params) {
		messages.error(bundleKey(key), params);
	}

	public void fatal(String key, Object... params) {
		messages.fatal(bundleKey(key), params);
	}
}
