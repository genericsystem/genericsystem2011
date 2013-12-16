package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.enterprise.event.Observes;
import javax.faces.bean.RequestScoped;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.myadmin.util.GsMessages;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.InvokeApplication;

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

	/**
	 * Creates new super cache.
	 * 
	 * @return string "HOME".
	 */
	public String mountNewCache() {
		cacheProvider.mountNewCache();

		messages.info("addCache");

		return "HOME";
	}

	/**
	 * Flush current cache into it's sub cache.
	 * 
	 * @return string "HOME".
	 */
	public String flushCurrentCache() {
		cacheProvider.flushCurrentCache();

		messages.info("flushCache", cacheProvider.getCurrentCache().getLevel());

		return "HOME";
	}

	/**
	 * Discards all changes in the current cache and returns to it's sub cache.
	 * 
	 * @return string "HOME".
	 */
	public String discardCurrentCache() {
		cacheProvider.discardCurrentCache();

		messages.info("discardCache", cacheProvider.getCurrentCache().getLevel());

		return "HOME";
	}

	/**
	 * Returns the formated timestamp of current cache (transaction).
	 * 
	 * @return the formated timestamp of current cache.
	 */
	public String getCurrentCacheTs() {
		return "";// new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(((CacheImpl) cacheProvider.getCurrentCache()).getTs() / Statics.MILLI_TO_NANOSECONDS)).toString();
	}

	public Cache getCache() {
		return cacheProvider.getCurrentCache();
	}

	public void setCache(Cache cache) {
		cacheProvider.setCurrentCache(cache);
	}

	/**
	 * Reset the timestamp of current transaction before the phase of RENDER_RESPONSE.
	 * 
	 * @param phaseEvent
	 *            - event of JSF phase.
	 * @throws ConstraintViolationException
	 */
	public void resetTransactionTs(@Observes @After @InvokeApplication PhaseEvent phaseEvent) throws ConstraintViolationException {
		((CacheImpl) cacheProvider.getCurrentCache()).refresh();
	}

}
