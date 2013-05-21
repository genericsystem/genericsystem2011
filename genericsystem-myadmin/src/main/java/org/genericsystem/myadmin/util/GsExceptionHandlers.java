package org.genericsystem.myadmin.util;

import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.genericsystem.exception.RollbackException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HandlesExceptions
public class GsExceptionHandlers {

	@Inject
	private FacesContext facesContext;

	// @Inject
	// private GsMessages gsMessages;

	@Inject
	GsRedirect redirect;
	protected static Logger log = LoggerFactory.getLogger(GsExceptionHandlers.class);

	void handleRollbackException(@Handles CaughtException<RollbackException> caught, HttpServletResponse response) {
		caught.handled();
		redirect.redirectOnException(caught.getException());
	}

	void handleViewExpiredException(@Handles CaughtException<ViewExpiredException> caught, HttpServletResponse response) {
		caught.handled();
		redirect.redirectWarn("viewExpiredException");
	}

	void handleRuntimeException(@Handles CaughtException<RuntimeException> caught, HttpServletResponse response) {
		caught.handled();
		redirect.redirectOnException(caught.getException());
	}
}