package org.genericsystem.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GSClassNotFoundTest extends AbstractTest {

	public void testClassValueNotFound() {
		String path = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(path).start();
		cache.setType(new MyValue());
		cache.flush();
		cache.getEngine().close();
		cache = GenericSystem.newCacheOnANewPersistentEngine(path).start();
		Type type = cache.getAllTypes().filter(new Filter<Type>() {

			@Override
			public boolean isSelected(Type element) {
				return element.getValue() instanceof byte[];
			}
		}).get(0);
		assert type != null;
	}

	// TODO test manuel
	public void testClassGenericNotFound() {
		String path = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(path, Vehicle.class).start();
		cache.getEngine().close();
	}

	@SystemGeneric
	public static class Vehicle extends GenericImpl {

	}

	public static class MyValue implements Serializable {

		private static final long serialVersionUID = 1380365016437512333L;

		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
			throw new ClassNotFoundException();
		}

	}

}