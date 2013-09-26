package org.genericsystem.file;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType;
import org.genericsystem.file.FileSystem.FileType.File;
import org.genericsystem.file.FileSystem.FileType.FileContent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.tree.NodeImpl;
import org.genericsystem.tree.TreeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
@Components(FileSystem.class)
@Dependencies(FileType.class)
@InstanceGenericClass(Directory.class)
public class FileSystem extends TreeImpl {
	protected static Logger log = LoggerFactory.getLogger(FileSystem.class);

	private static final String SEPARATOR = "/";

	private static final byte[] EMPTY = "<html/>".getBytes();

	public static class Directory extends NodeImpl {
		public <T extends File> Snapshot<T> getFiles() {
			return getHolders(getEngine().getCurrentCache().<Attribute> find(FileType.class));
		}

		public <T extends File> T getFile(String name) {
			return getHolderByValue(Statics.CONCRETE, getEngine().getCurrentCache().<Attribute> find(FileType.class), name);
		}

		public <T extends File> T addFile(String name) {
			return addFile(name, EMPTY);
		}

		public <T extends File> T addFile(String name, byte[] content) {
			T result = addHolder(getEngine().getCurrentCache().<Attribute> find(FileType.class), name);
			result.setContent(content);
			return result;
		}

		public <T extends File> T touchFile(String name) {
			return touchFile(name, EMPTY);
		}

		public <T extends File> T touchFile(String name, byte[] content) {
			T result = setHolder(getEngine().getCurrentCache().<Attribute> find(FileType.class), name);
			result.setContent(content);
			return result;
		}

		public <T extends Directory> Snapshot<T> getDirectories() {
			return getChildren();
		}

		public <T extends Directory> T getDirectory(String name) {
			return getChild(name);
		}

		public <T extends Directory> T addDirectory(String name) {
			return addNode(name);
		}

		public <T extends Directory> T touchDirectory(String name) {
			return setNode(name);
		}

		public String getShortPath() {
			return this.<String> getValue();
		}

		@Override
		public String getCategoryString() {
			return "Directory";
		}
	}

	@SystemGeneric
	@Components(FileSystem.class)
	@InstanceValueClassConstraint(String.class)
	@InstanceGenericClass(File.class)
	@Dependencies(FileContent.class)
	public static class FileType extends GenericImpl {
		@SystemGeneric
		@SingularConstraint
		@Components(FileType.class)
		@InstanceValueClassConstraint(byte[].class)
		public static class FileContent extends GenericImpl {

		}

		public static class File extends GenericImpl {
			public byte[] getContent() {
				return this.<byte[]> getValues(getEngine().getCurrentCache().<Attribute> find(FileContent.class)).get(0);
			}

			public <T extends Generic> T setContent(byte[] content) {
				return setValue(getEngine().getCurrentCache().<Attribute> find(FileContent.class), content);
			}

			public String getShortPath() {
				return this.<String> getValue();
			}

			@Override
			public String getCategoryString() {
				return "File";
			}
		}
	}

	public <T extends Directory> Snapshot<T> getRootDirectories() {
		return getRoots();
	}

	public <T extends Directory> T getRootDirectory(String name) {
		return getRootByValue(name);
	}

	public <T extends Directory> T addRootDirectory(String name) {
		if (getRootDirectory(name) != null)
			throw new IllegalStateException("Root directory : " + name + " already exists");
		return touchRootDirectory(name);
	}

	public <T extends Directory> T touchRootDirectory(String name) {
		return newRoot(name);
	}

	public byte[] getFileContent(String resource) {
		if (resource.startsWith(SEPARATOR))
			resource = resource.substring(1);
		String[] files = resource.split(SEPARATOR);
		Directory directory = getRootDirectory(files[0]);
		if (directory == null)
			return null;
		for (int i = 1; i < files.length - 1; i++) {
			directory = directory.getDirectory(files[i]);
			if (directory == null)
				return null;
		}
		File file = directory.getFile(files[files.length - 1]);
		if (file == null)
			return null;
		return file.getContent();
	}

	public <T extends File> T touchFile(String resource) {
		return touchFile(resource, EMPTY);
	}

	public <T extends File> T touchFile(String resource, byte[] content) {
		if (resource.startsWith(SEPARATOR))
			resource = resource.substring(1);
		String[] files = resource.split(SEPARATOR);
		Directory directory = touchRootDirectory(files[0]);
		for (int i = 1; i < files.length - 1; i++)
			directory = directory.touchDirectory(files[i]);
		return directory.touchFile(files[files.length - 1], content);
	}
}
