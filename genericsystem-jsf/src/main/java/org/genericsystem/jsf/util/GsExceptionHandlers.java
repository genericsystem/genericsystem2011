package org.genericsystem.jsf.util;

import javax.faces.application.ViewExpiredException;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.genericsystem.exception.RollbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExceptionHandler
public class GsExceptionHandlers {

	@Inject
	GsRedirect redirect;
	protected static Logger log = LoggerFactory.getLogger(GsExceptionHandlers.class);

	void handleRollbackException(@Handles ExceptionEvent<RollbackException> caught) {
		caught.handled();
		redirect.redirectOnException(caught.getException());
	}

	void handleViewExpiredException(@Handles ExceptionEvent<ViewExpiredException> caught) {
		caught.handled();
		redirect.redirectWarn("viewExpiredException");
	}

	void handleRuntimeException(@Handles ExceptionEvent<RuntimeException> caught) {
		caught.handled();
		redirect.redirectOnException(caught.getException());
	}
}