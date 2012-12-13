package org.genericsystem.impl.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.GenericSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EngineProvider implements Serializable {

	private static final long serialVersionUID = -3997634615567607258L;

	private static final Logger log = LoggerFactory.getLogger(EngineProvider.class);

	private static String DIRECTORY_PATH = System.getenv("HOME") + "/test/snapshot_save";

	private Engine engine;

	@PostConstruct
	public void init() {
		log.info("Init engine : " + System.identityHashCode(engine));
		engine = GenericSystem.newPersistentEngine(DIRECTORY_PATH);
	}

	@Named("engine")
	@Produces
	public Engine getEngine() {
		return engine;
	}

	@PreDestroy
	public void preDestroy() {
		log.info("Destroy engine : " + System.identityHashCode(engine));
		engine.close();
		engine = null;
	}
}
