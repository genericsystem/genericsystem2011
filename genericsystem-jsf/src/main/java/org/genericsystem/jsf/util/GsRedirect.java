package org.genericsystem.jsf.util;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GsRedirect {
	protected static Logger log = LoggerFactory.getLogger(GsRedirect.class);

	@Inject
	private FacesContext facesContext;

	@Inject
	private GsMessages messages;

	public void redirect() {
		try {
			facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath());
		} catch (IOException e) {
			log.error(GsMessages.toString(e));
		}
	}

	public void redirectError(String key, Object... params) {
		messages.redirectError(key, params);
		redirect();
	}

	public void redirectWarn(String key, Object... params) {
		messages.redirectWarn(key, params);
		redirect();
	}

	public void redirectInfo(String key, Object... params) {
		messages.redirectInfo(key, params);
		redirect();
	}

	public void redirectOnException(Throwable t) {
		messages.redirectThrowable(t);
		redirect();
	}
}
