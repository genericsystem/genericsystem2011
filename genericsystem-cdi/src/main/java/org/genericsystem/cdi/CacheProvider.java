package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;

@SessionScoped
public class CacheProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	private transient Engine engine;

	private Cache currentCache;

	@PostConstruct
	public void init() {
		currentCache = engine.newCache().start();
	}

	public void mountNewCache() {
		currentCache = currentCache.mountNewCache();
	}

	public void flushCurrentCache() {
		currentCache = currentCache.flushAndUnmount();
	}

	public void discardCurrentCache() {
		currentCache = currentCache.discardAndUnmount();
	}

	@Produces
	public Cache getCurrentCache() {
		return currentCache;
	}

	public void setCurrentCache(Cache cache) {
		this.currentCache = cache;
	}

}
