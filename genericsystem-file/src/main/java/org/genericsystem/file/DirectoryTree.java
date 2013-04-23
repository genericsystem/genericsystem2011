package org.genericsystem.file;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.file.DirectoryTree.FileType;
import org.genericsystem.file.DirectoryTree.FileType.FileContent;

@SystemGeneric
@InstanceClassConstraint(String.class)
@Components(DirectoryTree.class)
@Dependencies(FileType.class)
public class DirectoryTree {
	@SystemGeneric
	@Components(DirectoryTree.class)
	@InstanceClassConstraint(String.class)
	@Dependencies(FileContent.class)
	public static class FileType {
		@SystemGeneric
		@SingularConstraint
		@Components(FileType.class)
		public static class FileContent {

		}
	}
}
