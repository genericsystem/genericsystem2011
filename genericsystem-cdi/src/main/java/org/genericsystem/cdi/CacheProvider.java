package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.genericsystem.core.Cache;

@SessionScoped
public class CacheProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	private transient EngineProvider engineProvider;

	private transient Cache cache;

	@PostConstruct
	public void init() {
		cache = engineProvider.getEngine().newCache();
	}

	public Cache getCache() {
		return cache;
	}

}
