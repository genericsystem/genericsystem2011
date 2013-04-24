package org.genericsystem.file;

import java.util.Objects;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.InstanceClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.file.DirectoryTree.Directory;
import org.genericsystem.file.DirectoryTree.FileType;
import org.genericsystem.file.DirectoryTree.FileType.File;
import org.genericsystem.file.DirectoryTree.FileType.FileContent;
import org.genericsystem.generic.Attribute;

@SystemGeneric
@InstanceValueClassConstraint(String.class)
@Components(DirectoryTree.class)
@Dependencies(FileType.class)
@InstanceClass(Directory.class)
public class DirectoryTree extends GenericImpl {

	public static class Directory extends GenericImpl {
		public <T extends File> Snapshot<T> getFiles(Context context) {
			return getHolders(context, context.<Attribute> find(FileType.class));
		}

		public <T extends File> T getFile(Context context, final String name) {
			return this.<T> getFiles(context).filter(new Filter<T>() {
				@Override
				public boolean isSelected(T element) {
					return Objects.equals(name, element.getValue());
				}
			}).first();
		}

		public <T extends File> T addFile(Cache cache, String name, byte[] content) {
			if (getFile(cache, name) != null)
				throw new IllegalStateException("File : " + name + " already exists");
			T result = setHolder(cache, cache.<Attribute> find(FileType.class), name);
			result.setContent(cache, content);
			return result;
		}

		public <T extends Directory> Snapshot<T> getDirectories(Context context) {
			return getHolders(context, context.<Attribute> find(DirectoryTree.class));
		}

		public <T extends Directory> T getDirectory(Context context, final String name) {
			return this.<T> getDirectories(context).filter(new Filter<T>() {
				@Override
				public boolean isSelected(T element) {
					return Objects.equals(name, element.getValue());
				}
			}).first();
		}

		public <T extends Directory> T addDirectory(Cache cache, String name) {
			if (getDirectory(cache, name) != null)
				throw new IllegalStateException("Directory : " + name + " already exists");
			return setHolder(cache, cache.<Attribute> find(DirectoryTree.class), name);
		}
	}

	@SystemGeneric
	@Components(DirectoryTree.class)
	@InstanceValueClassConstraint(String.class)
	@InstanceClass(File.class)
	@Dependencies(FileContent.class)
	public static class FileType extends GenericImpl {
		@SystemGeneric
		@SingularConstraint
		@Components(FileType.class)
		@InstanceValueClassConstraint(byte[].class)
		public static class FileContent extends GenericImpl {

		}

		public static class File extends GenericImpl {
			public byte[] getContent(Context context) {
				return this.<byte[]> getValues(context, context.<Attribute> find(FileContent.class)).first();
			}

			public <T extends Generic> T setContent(Cache cache, byte[] content) {
				return setValue(cache, cache.<Attribute> find(FileContent.class), content);
			}
		}
	}

	public <T extends Directory> Snapshot<T> getRootDirectories(Context context) {
		return getInstances(context);
	}

	public <T extends Directory> T getRootDirectory(Context context, final String name) {
		return this.<T> getRootDirectories(context).filter(new Filter<T>() {
			@Override
			public boolean isSelected(T element) {
				return Objects.equals(name, element.getValue());
			}
		}).first();
	}

	public <T extends Directory> T addRootDirectory(Cache cache, String name) {
		if (getRootDirectory(cache, name) != null)
			throw new IllegalStateException("Root directory : " + name + " already exists");
		return newRoot(cache, name);
	}
}
