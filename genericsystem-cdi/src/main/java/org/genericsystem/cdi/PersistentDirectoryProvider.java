package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;

/**
 * Persistence is not activated by default if you want to persist, you have to set a specialized mock persistentDirectoryProvider in your project :
 * 
 * @Specializes public class MockPersitentDirectoryProvider extends PersitentDirectoryProvider { @Override String getDirectoryPath() { return DIRECTORY_PATH; } }
 * 
 * @author Nicolas Feybesse
 * 
 */
@ApplicationScoped
public class PersistentDirectoryProvider {
	public static final String DIRECTORY_PATH = System.getenv("HOME") + "/test/genericsystem";

	public String getDirectoryPath() {
		return null;// no persistence by default
	}
}
