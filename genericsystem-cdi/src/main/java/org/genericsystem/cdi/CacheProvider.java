package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;

@SessionScoped
public class CacheProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	private transient Engine engine;

	private transient Cache cache;

	@PostConstruct
	public void init() {
		cache = engine.newCache();
	}

	public void newSuperCache() {
		Cache superCache = cache.newSuperCache().start();
		cache = superCache;
	}

	public void saveCache() {
		cache.flush();
		AbstractContext subContext = ((CacheImpl) cache).getSubContext();
		if (subContext instanceof Cache)
			cache = (Cache) subContext;
	}

	public void discardCache() {
		cache.clear();
		AbstractContext subContext = ((CacheImpl) cache).getSubContext();
		if (subContext instanceof Cache)
			cache = (Cache) subContext;
	}

	@Produces
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
