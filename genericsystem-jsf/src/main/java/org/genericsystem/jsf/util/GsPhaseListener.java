package org.genericsystem.jsf.util;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.BeforePhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsPhaseListener {

	private static final Logger log = LoggerFactory.getLogger(GsPhaseListener.class);

	public void observeBefore(@Observes @BeforePhase(JsfPhaseId.ANY_PHASE) PhaseEvent e) {
		log.info("=========== begin :" + e.getPhaseId().toString());
	}

	public void observeAfter(@Observes @AfterPhase(JsfPhaseId.ANY_PHASE) PhaseEvent e) {
		log.info("===========  end   :" + e.getPhaseId().toString());
	}
}