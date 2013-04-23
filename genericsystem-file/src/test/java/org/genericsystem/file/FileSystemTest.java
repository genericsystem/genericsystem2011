package org.genericsystem.file;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.file.AbstractTest.RollbackCatcher;
import org.genericsystem.file.DirectoryTree.FileType;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class FileSystemTest {

	@Test
	public void testFileSystemAndDirectoryTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
		Type fileSystem = cache.find(FileType.class);
		Tree directoryTree = cache.find(DirectoryTree.class);
		assert cache.isAlive(fileSystem);
		assert cache.isAlive(directoryTree);
	}

	// // Constraint order problem: NotDuplicated vs Unique
	// public void testDirectoryNameUniqueInASameDirectory() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
	// Tree directoryTree = cache.find(DirectoryTree.class);
	// final Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
	// rootDirectory.setNode(cache, "middleware1");
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// rootDirectory.setNode(cache, "middleware1");
	// }
	// }.assertIsCausedBy(UniqueConstraintViolationException.class);
	// }

	@SuppressWarnings("unused")
	public void testDirectoryNameNotUniqueInDifferentDirectories() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
		Tree directoryTree = cache.find(DirectoryTree.class);
		Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
		Node directory1 = rootDirectory.setNode(cache, "directory1");
		Node directory2 = rootDirectory.setNode(cache, "directory2");
		directory2.setNode(cache, "directory1"); // No Exception
	}

	public void testDirectoryNameValueClassViolation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
		Tree directoryTree = cache.find(DirectoryTree.class);
		assert directoryTree.getConstraintClass(cache) != null;
		final Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				rootDirectory.setNode(cache, 2L);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	// //
	// // // Constraint order problem: NotDuplicated vs Unique
	// public void testFileNameUniqueInASameDirectory() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
	// Tree directoryTree = cache.find(DirectoryTree.class);
	// Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
	// final Node directory = rootDirectory.setNode(cache, "directory");
	// final Attribute fileSystem = cache.find(FileType.class);
	// directory.setValue(cache, fileSystem, "test.html");
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// directory.setValue(cache, fileSystem, "test.html");
	// }
	// }.assertIsCausedBy(UniqueConstraintViolationException.class);
	// }

	public void testFileNameNotUniqueInDifferentDirectories() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
		Tree directoryTree = cache.find(DirectoryTree.class);
		Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
		Node directory1 = rootDirectory.setNode(cache, "directory1");
		Node directory2 = rootDirectory.setNode(cache, "directory2");
		final Attribute file = cache.find(FileType.class);
		Holder file1 = directory1.setValue(cache, file, "test.hmtl");
		Holder file2 = directory2.setValue(cache, file, "test.hmtl");// No Exception
		assert file1 != file2;
	}

	public void testFileNameValueClassViolation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(DirectoryTree.class);
		Tree directoryTree = cache.find(DirectoryTree.class);
		final Node rootDirectory = directoryTree.newRoot(cache, "rootDirectory");
		final Attribute fileSystem = cache.find(FileType.class);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				rootDirectory.setValue(cache, fileSystem, 2L);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}
}
