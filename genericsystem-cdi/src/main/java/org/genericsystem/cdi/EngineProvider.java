package org.genericsystem.cdi;

import java.io.Serializable;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EngineProvider implements Serializable {

	private static final long serialVersionUID = 4557162364822967228L;

	protected static Logger log = LoggerFactory.getLogger(EngineProvider.class);

	private transient Engine engine;

	@Inject
	private UserClasses userClasses;

	@PostConstruct
	public void init() {
		log.info("############################################## START ENGINE #######################################################################");
		log.info("with userClasses : " + Arrays.toString(userClasses.getUserClassesArray()));
		engine = GenericSystem.newInMemoryEngine(userClasses.getUserClassesArray());
		log.info("#######################################################################################################################################");
	}

	@Produces
	public Engine getEngine() {
		return engine;
	}
}
