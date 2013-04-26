package org.genericsystem.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@Specializes
@ApplicationScoped
public class MockPersitentDirectoryProvider extends PersitentDirectoryProvider {

	@Override
	String getDirectoryPath() {
		return null;
	}
}
