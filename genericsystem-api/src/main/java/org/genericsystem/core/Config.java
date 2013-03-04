package org.genericsystem.core;

import org.genericsystem.core.Factory.DefaultFactory;

/**
 * Config.
 * 
 * @author Nicolas Feybesse
 */
public class Config {

	/**
	 * Directory path of the persistence.
	 */
	private String directoryPath;

	/**
	 * The Factory.
	 */
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
