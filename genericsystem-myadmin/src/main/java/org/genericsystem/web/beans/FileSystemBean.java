package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.genericsystem.core.Cache;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@javax.enterprise.context.RequestScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	@Inject
	private transient Cache cache;

	public List<Directory> getRootDirectories() {
		return cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).toList();
	}

	public List<Directory> getDirectories(final Directory directory) {
		return directory.getDirectories(cache).toList();
	}

	public List<File> getFiles(final Directory directory) {
		return directory.getFiles(cache).toList();
	}

}
