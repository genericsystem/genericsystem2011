package org.genericsystem.jsf.util;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsPhaseListener {

	private static final Logger log = LoggerFactory.getLogger(GsPhaseListener.class);

	public void observeBefore(@Observes @Before PhaseEvent e) {
		log.info("=========== begin :" + e.getPhaseId().toString());
	}

	public void observeAfter(@Observes @After PhaseEvent e) {
		log.info("===========  end   :" + e.getPhaseId().toString());
	}
}