package org.genericsystem.web.util;

import java.util.Objects;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.genericsystem.exception.RollbackException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.Precedence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HandlesExceptions
public class ExceptionHandlers {

	@Inject
	private ExternalContext context;

	@Inject
	private FacesContext facesContext;

	@Inject
	private GsMessages gsMessages;

	protected static Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	private static String toString(StackTraceElement... objects) {
		String s = "\n";
		for (Object object : objects)
			s += Objects.toString(object) + "\n";
		return s;
	}

	void handleRollbackException(@Handles CaughtException<RollbackException> caught, HttpServletResponse response) {
		log.error(caught.getException().toString() + "\n" + toString(caught.getException().getStackTrace()));
		gsMessages.redirectStringError(caught.getException().getMessage());
		facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "/gsmyadmin/pages/index.xhtml");
	}

	void handleViewExpiredException(@Handles CaughtException<ViewExpiredException> caught, HttpServletResponse response) {
		log.error(caught.getException().toString() + "\n" + toString(caught.getException().getStackTrace()));
		gsMessages.redirectError("viewExpiredException");
		facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "/gsmyadmin/pages/index.xhtml");
	}

	void handleRuntimeException(@Handles(precedence = Precedence.LOW) CaughtException<RuntimeException> caught, HttpServletResponse response) {
		log.error(caught.getException().toString() + "\n" + toString(caught.getException().getStackTrace()));
		gsMessages.redirectStringError(caught.getException().getMessage());
		facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "/gsmyadmin/pages/index.xhtml");
	}
}