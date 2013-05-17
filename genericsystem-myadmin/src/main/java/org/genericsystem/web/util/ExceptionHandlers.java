package org.genericsystem.web.util;

import java.io.IOException;
import java.util.Objects;
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
public class ExceptionHandlers {

	@Inject
	private FacesContext facesContext;

	@Inject
	private GsMessages gsMessages;

	protected static Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	private static String toString(Throwable throwable) {
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

	void handleRollbackException(@Handles CaughtException<RollbackException> caught, HttpServletResponse response) {

		log.error("\n" + toString(caught.getException()));
		gsMessages.redirectStringError(caught.getException().toString());
		caught.handled();
		try {
			facesContext.getExternalContext().redirect("/gsmyadmin/pages/index.xhtml");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		// facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "HOME");
	}

	void handleViewExpiredException(@Handles CaughtException<ViewExpiredException> caught, HttpServletResponse response) {
		log.error("\n" + toString(caught.getException()));
		gsMessages.redirectError("viewExpiredException");
		caught.handled();
		try {
			facesContext.getExternalContext().redirect("/gsmyadmin/pages/index.xhtml");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		// facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "HOME");
	}

	void handleRuntimeException(@Handles CaughtException<RuntimeException> caught, HttpServletResponse response) {
		log.error("\n" + toString(caught.getException()));
		gsMessages.redirectStringError(caught.getException().getMessage());
		caught.handled();
		try {
			facesContext.getExternalContext().redirect("/gsmyadmin/pages/index.xhtml");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		// facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "HOME");
	}
}