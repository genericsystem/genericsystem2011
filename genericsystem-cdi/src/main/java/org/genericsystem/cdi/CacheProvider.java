package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;

@SessionScoped
public class CacheProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	@Memory
	private transient Engine engineMemory;

	@Inject
	@Persistent
	private transient Engine enginePersistent;

	private transient Cache cacheMemory;

	private transient Cache cachePersistent;

	@PostConstruct
	// TODO KK => 2 cache for 1 session ???
	public void init() {
		cacheMemory = engineMemory.newCache();
		cachePersistent = enginePersistent.newCache();
	}

	@Produces
	@Named("getCacheWithMemoryEngine")
	public Cache getCacheWithMemoryEngine() {
		return cacheMemory;
	}

	@Produces
	@Named("getCacheWithPersistentEngine")
	public Cache getCacheWithPersistentEngine() {
		return cachePersistent;
	}
}
