package org.genericsystem.web.beans;

import java.io.IOException;
import java.util.Arrays;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.jboss.seam.international.status.Messages;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@HandlesExceptions
public class ExceptionHandlers {

	@Inject
	Messages messages;

	protected static Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	void handleAll(@Handles CaughtException<Throwable> caught, HttpServletResponse response) {
		messages.error(Arrays.toString(caught.getException().getStackTrace()));
		log.error(Arrays.toString(caught.getException().getStackTrace()));
		try {
			response.sendError(500, "You've been caught by Catch!");
		} catch (IOException e) {
			log.error(Arrays.toString(e.getStackTrace()));
		}
	}
}