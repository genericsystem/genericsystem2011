package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericSystem;

@ApplicationScoped
public class EngineProvider implements Serializable {

	private static final long serialVersionUID = 4557162364822967228L;

	private transient Engine engine;

	@PostConstruct
	public void init() {
		engine = GenericSystem.newInMemoryEngine();
	}

	public Engine getEngine() {
		return engine;
	}
}
