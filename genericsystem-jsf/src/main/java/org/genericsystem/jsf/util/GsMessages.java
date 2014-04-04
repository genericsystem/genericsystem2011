package org.genericsystem.jsf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.message.Message;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.impl.message.DefaultMessage;
import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class GsMessages implements Serializable {

	private static final long serialVersionUID = 7666964394295933934L;
	protected static Logger log = LoggerFactory.getLogger(GsMessages.class);

	public static String toString(Throwable throwable) {
		String s = "";
		while (throwable != null) {
			s += throwable + "\n";
			for (Object object : throwable.getStackTrace())
				s += Objects.toString(object) + "\n";
			throwable = throwable.getCause();
			s += "\n";
		}
		return s;
	}

	// @Inject
	transient FacesContext context;

	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
	}

	@Inject
	MessageContext messageContext;

	private static final String MESSAGES_BUNDLE_NAME = "/bundles/messages";

	private static final String INFOS_BUNDLE_NAME = "/bundles/infos";

	private List<Message> messagesToRedirect = new ArrayList<>();

	public void restoreMessages(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent e) {
		for (Message message : messagesToRedirect) {
			context.addMessage(null, new FacesMessage(message.toString()));
			log.info(message.toString());
		}
		messagesToRedirect.clear();
	}

	public void redirectError(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.error(message.toString());
		messagesToRedirect.add(message);
	}

	public void redirectWarn(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.warn(message.toString());
		messagesToRedirect.add(message);
	}

	public void redirectInfo(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.info(message.toString());
		messagesToRedirect.add(message);
	}

	public void redirectThrowable(Throwable t) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(t.toString());
		log.error("\n" + toString(t));
		messagesToRedirect.add(message);
	}

	public String getMessage(String key, Object... params) {
		return new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params).toString();
	}

	public String getInfos(String key, Object... params) {
		return new DefaultMessage(messageContext.messageSource(INFOS_BUNDLE_NAME)).template(key).argument(params).toString();
	}

	public void info(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.info(message.toString());
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message.toString(), ""));
	}

	public void warn(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.warn(message.toString());
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message.toString(), ""));
	}

	public void error(String key, Object... params) {
		Message message = new DefaultMessage(messageContext.messageSource(MESSAGES_BUNDLE_NAME)).template(key).argument(params);
		log.error(message.toString());
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message.toString(), ""));
	}
}
