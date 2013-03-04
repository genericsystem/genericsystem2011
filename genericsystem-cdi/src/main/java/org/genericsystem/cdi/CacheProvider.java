package org.genericsystem.cdi;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;

/**
 * @author Nicolas Feybesse
 * 
 */
@SessionScoped
public class CacheProvider implements Serializable {
	
	private static final long serialVersionUID = 5201003234496546928L;
	
	private Cache cache;
	@Inject
	private transient Engine engine;
	
	@PostConstruct
	public void init() {
		cache = engine.newCache();
	}
	
	@Produces
	@Named("cache")
	public Cache getCache() {
		return cache;
	}
}