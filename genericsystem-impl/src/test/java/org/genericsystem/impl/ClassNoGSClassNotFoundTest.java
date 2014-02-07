package org.genericsystem.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import org.genericsystem.core.AbstractWriter.AbstractLoader.GSClassNotFound;
import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ClassNoGSClassNotFoundTest extends AbstractTest {

	public void testType() {
		String path = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(path).start();
		cache.setType(new Vehicle());
		cache.flush();
		cache.getEngine().close();
		cache = GenericSystem.newCacheOnANewPersistentEngine(path).start();
		Type type = cache.getAllTypes().filter(new Filter<Type>() {

			@Override
			public boolean isSelected(Type element) {
				return element.getValue() instanceof GSClassNotFound;
			}
		}).get(0);
		assert ((GSClassNotFound) type.getValue()).getClassName().equals("org.genericsystem.impl.ClassNoGSClassNotFoundTest$Vehicle");
		cache.flush();
		cache.getEngine().close();
		cache = GenericSystem.newCacheOnANewPersistentEngine(path).start();
		assert cache.getAllTypes().filter(new Filter<Type>() {

			@Override
			public boolean isSelected(Type element) {
				return element.getValue() instanceof GSClassNotFound;
			}
		}).isEmpty();
	}

	public static class Vehicle implements Serializable {

		private static final long serialVersionUID = 1380365016437512333L;

		private void writeObject(ObjectOutputStream oos) throws IOException {
		}

		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
			throw new ClassNotFoundException("org.genericsystem.impl.ClassNoGSClassNotFoundTest$Vehicle");
		}

		@Override
		public String toString() {
			return "Vehicle";
		}

	}

}