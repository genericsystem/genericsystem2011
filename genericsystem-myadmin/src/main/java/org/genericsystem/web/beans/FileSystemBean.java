package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.web.util.AbstractSequentialList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	@Inject
	private transient Cache cache;

	/* rich:tree */
	public List<Directory> getRootDirectories() {
		log.info("getRootDirectories " + cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache));
		return new AbstractSequentialList<Directory>() {
			@Override
			public Iterator<Directory> iterator() {
				return cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).iterator();
			}
		};
	}

	public List<Directory> getDirectories(final Directory directory) {
		log.info(directory + ".getDirectories(cache) " + directory.getDirectories(cache));
		return new AbstractSequentialList<Directory>() {
			@Override
			public Iterator<Directory> iterator() {
				return directory.getDirectories(cache).iterator();
			}
		};
	}
}
