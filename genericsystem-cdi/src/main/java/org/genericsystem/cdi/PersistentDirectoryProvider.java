package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersistentDirectoryProvider {
	public static final String DIRECTORY_PATH = System.getenv("HOME") + "/test/genericsystem";

	public String getDirectoryPath() {
		return DIRECTORY_PATH;
	}
}
