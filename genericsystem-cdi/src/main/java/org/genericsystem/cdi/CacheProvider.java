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

	private transient Cache cache;

	@PostConstruct
	public Cache init() {
		cache = engine.newCache();
		return cache;
	}

	@Produces
	public Cache getCache() {
		return cache;
	}

}
