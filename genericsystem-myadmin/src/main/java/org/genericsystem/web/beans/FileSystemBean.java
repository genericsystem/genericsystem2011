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

	/* rich:tree */
	public List<Directory> getRootDirectories() {
		log.info("getRootDirectories " + cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).toList());
		return cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).toList();
	}

	public List<Directory> getDirectories(final Directory directory) {
		log.info(directory + ".getDirectories(cache) " + directory.getDirectories(cache));
		return directory.getDirectories(cache).toList();
	}

	public List<File> getFiles(final Directory directory) {
		log.info(directory + ".getFiles(cache) " + directory.getFiles(cache));
		return directory.getFiles(cache).toList();
	}

	public String log(String str) {
		log.info(str);
		return str;
	}
}
