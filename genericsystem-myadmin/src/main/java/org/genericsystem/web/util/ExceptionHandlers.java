package org.genericsystem.web.util;

import java.io.IOException;
import java.util.Objects;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExternalContext;
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
	private RedirectMessages redirectMessage;

	protected static Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	private static String toString(StackTraceElement... objects) {
		String s = "\n";
		for (Object object : objects)
			s += Objects.toString(object) + "\n";
		return s;
	}

	void handleAll(@Handles CaughtException<Throwable> caught, HttpServletResponse response) {
		redirectMessage.addErrorMessage("viewExpiredException");
		log.error(toString(caught.getException().getStackTrace()));
		try {
			if (caught.getException() instanceof ViewExpiredException)
				context.redirect("/gsmyadmin/pages/index.xhtml");
			else
				response.sendError(500, "You've been caught by Catch! : " + toString(caught.getException().getStackTrace()));
		} catch (IOException e) {
			log.error(toString(e.getStackTrace()));
		}
	}
}