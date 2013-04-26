package org.genericsystem.cdi;

import java.io.Serializable;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
	private UserClassesProvider userClassesProvider;

	@Inject
	PersitentDirectoryProvider persistentDirectoryProvider;

	@PostConstruct
	public void init() {

		log.info("");
		log.info("   _____________   ____________    ________ ");
		log.info("  |___________ /  / ____/  ___/   /________|");
		log.info("  |___________/  / /___ \\__  \\   /_________|");
		log.info("  |__________/  / /_  /___/  /  /__________|");
		log.info("  |_________/   \\____/______/  /___________|");
		log.info("");
		log.info("");
		log.info("-----------------------------------------------------------------------------------------------");
		log.info("|  directory path : " + persistentDirectoryProvider.getDirectoryPath());
		log.info("|  userClasses : " + Arrays.toString(userClassesProvider.getUserClassesArray()));
		log.info("-----------------------------------------------------------------------------------------------");
		engine = GenericSystem.newPersistentEngine(persistentDirectoryProvider.getDirectoryPath(), userClassesProvider.getUserClassesArray());
	}

	@Produces
	public Engine getEngine() {
		return engine;
	}

	@PreDestroy
	public void destoy() {
		log.info("$$$$$$$$$$$$$$ STOP GS ENGINE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		engine.close();
		engine = null;
	}
}
