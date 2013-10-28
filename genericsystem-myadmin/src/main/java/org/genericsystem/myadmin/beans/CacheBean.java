package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.myadmin.util.GsMessages;

/**
 * Bean for management of cache via GUI of MyAdmin.
 * 
 * @author Alexei KLENIN - aklenin@middlewarefactory.com
 */
@Named
@RequestScoped
public class CacheBean implements Serializable {

	private static final long serialVersionUID = 1752488068961882618L;

	@Inject private GsMessages messages;
	@Inject private transient CacheProvider cacheProvider;

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
		return new SimpleDateFormat(Statics.INT_TS_PATTERN).format(new Date(((CacheImpl) cacheProvider.getCurrentCache()).getTs() / Statics.MILLI_TO_NANOSECONDS)).toString();
	}

	public Cache getCache() {
		return cacheProvider.getCurrentCache();
	}

	public void setCache(Cache cache) {
		cacheProvider.setCurrentCache(cache);
	}
}
