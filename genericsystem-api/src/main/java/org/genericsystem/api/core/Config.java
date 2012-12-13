package org.genericsystem.api.core;

import org.genericsystem.api.core.Factory.DefaultFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Config {

	private String directoryPath;

	private Factory factory;

	public Config() {
		this(null, new DefaultFactory());
	}

	public Config(String directoryPath) {
		this(directoryPath, new DefaultFactory());
	}

	public Config(String directoryPath, Factory factory) {
		this.directoryPath = directoryPath;
		this.factory = factory;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public Factory getFactory() {
		return factory;
	}
}
