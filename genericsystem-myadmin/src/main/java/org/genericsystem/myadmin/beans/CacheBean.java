package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Cache;
import org.genericsystem.myadmin.util.GsMessages;

/**
 * Bean for managedement of cache via GUI of MyAdmin.
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
	public String newSuperCache() {
		cacheProvider.newSuperCache();

		messages.info("addCache");

		return "HOME";
	}

	/**
	 * Flush current cache into it's sub cache.
	 * 
	 * @return string "HOME".
	 */
	public String saveCache() {
		cacheProvider.saveCache();

		messages.info("flushCache", cacheProvider.getCache().getLevel());

		return "HOME";
	}

	/**
	 * Discards all changes in the current cache and returns to it's sub cache.
	 * 
	 * @return string "HOME".
	 */
	public String discardCache() {
		cacheProvider.discardCache();

		messages.info("discardCache", cacheProvider.getCache().getLevel());

		return "HOME";
	}

	public Cache getCache() {
		return cacheProvider.getCache();
	}

	public void setCache(Cache cache) {
		cacheProvider.setCache(cache);
	}
}
