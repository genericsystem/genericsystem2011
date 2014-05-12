package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.faces.bean.RequestScoped;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;
import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.jsf.util.GsMessages;

/**
 * Bean for management of cache via GUI of MyAdmin.
 * 
 * @author Alexei KLENIN - aklenin@middlewarefactory.com
 */
@Named
@RequestScoped
public class CacheBean implements Serializable {

	private static final long serialVersionUID = 1752488068961882618L;

	@Inject
	private GsMessages messages;
	@Inject
	private transient CacheProvider cacheProvider;

	public String mountNewCache() {
		cacheProvider.mountNewCache();

		messages.info("addCache");

		return "HOME";
	}

	public String flushCurrentCache() {
		cacheProvider.flushCurrentCache();

		messages.info("flushCache", "" + cacheProvider.getCurrentCache().getLevel());

		return "HOME";
	}

	public String discardCurrentCache() {
		cacheProvider.discardCurrentCache();

		messages.info("discardCache", "" + cacheProvider.getCurrentCache().getLevel());

		return "HOME";
	}

	public String getCurrentCacheTs() {
		return new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(((CacheImpl) cacheProvider.getCurrentCache()).getTs() / Statics.MILLI_TO_NANOSECONDS)).toString();
	}

	public Cache getCache() {
		return cacheProvider.getCurrentCache();
	}

	public void setCache(Cache cache) {
		cacheProvider.setCurrentCache(cache);
	}

	public void resetTransactionTs(@Observes @AfterPhase(JsfPhaseId.INVOKE_APPLICATION) PhaseEvent phaseEvent) throws ConstraintViolationException {
		((CacheImpl) cacheProvider.getCurrentCache()).refresh();
	}

}
