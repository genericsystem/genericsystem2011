package org.genericsystem.web.util;

import java.util.Objects;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
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

	void handleAll(@Handles CaughtException<Throwable> caught, HttpServletResponse response) {
		log.error(caught.getException().toString() + "\n" + toString(caught.getException().getStackTrace()));
		if (caught.getException() instanceof ViewExpiredException)
			gsMessages.redirectError("viewExpiredException");
		else
			gsMessages.redirectStringError(caught.getException().getMessage());
		facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "/gsmyadmin/pages/index.xhtml");
	}
}