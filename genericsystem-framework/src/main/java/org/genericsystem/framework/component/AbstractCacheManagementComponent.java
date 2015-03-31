package org.genericsystem.framework.component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import org.apache.deltaspike.jsf.api.listener.phase.AfterPhase;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseId;
import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.jsf.util.GsMessages;

public class AbstractCacheManagementComponent extends AbstractComponent {

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

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

	public Integer getLevelCasheProvider() {
		return cacheProvider.getCurrentCache().getLevel();
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean isDirty) {

	}
}
