package org.genericsystem.jsf.example.persistent;

import javax.enterprise.inject.Specializes;

import org.genericsystem.cdi.PersistentDirectoryProvider;

@Specializes
public class MockPersistentDirectoryProvider extends PersistentDirectoryProvider {

	@Override
	public String getDirectoryPath() {
		return DIRECTORY_PATH;
	}
}
