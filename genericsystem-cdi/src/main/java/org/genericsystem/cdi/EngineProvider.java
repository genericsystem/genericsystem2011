package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericSystem;

@ApplicationScoped
public class EngineProvider implements Serializable {

	private static final long serialVersionUID = -3997634615567607258L;

	private static String DIRECTORY_PATH = System.getenv("HOME") + "/test/snapshot_save";

	private transient Engine engineMemory;

	private transient Engine enginePersistent;

	@PostConstruct
	public void init() {
		engineMemory = GenericSystem.newInMemoryEngine();
		enginePersistent = GenericSystem.newPersistentEngine(DIRECTORY_PATH);
	}

	@Produces
	@Memory
	public Engine getInMemoryEngine() {
		return engineMemory;
	}

	@Produces
	@Persistent
	public Engine getPersistentEngine() {
		return enginePersistent;
	}

}
