package org.genericsystem.web.util;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;

import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseListener {

	private static final Logger log = LoggerFactory.getLogger(PhaseListener.class);

	public void observeBefore(@Observes @Before PhaseEvent e) {
		log.info("\n\n\n---------------------------------" + e.getPhaseId().toString() + "-----------------------------------------\n");
	}

	public void observeAfter(@Observes @After PhaseEvent e) {
		log.info("\n----------------------------------------------------------------------------------------------------------------\n\n\n");
	}

}