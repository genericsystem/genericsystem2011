package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersitentDirectoryProvider {
	private String directoryPath = System.getenv("HOME") + "/test/genericsystem";

	String getDirectoryPath() {
		return directoryPath;
	}
}
