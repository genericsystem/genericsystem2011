package org.genericsystem.web.beans;

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
public class FileSystemBean {

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	@Inject
	private Cache cache;

	/* rich:tree */
	public synchronized List<Directory> getRootDirectories() {
		log.info("AAA");
		FileSystem find = cache.<FileSystem> find(FileSystem.class);
		log.info("BBB");
		return find.getRootDirectories(cache).toList();
	}

	public List<Directory> getDirectories(final Directory directory) {
		return new AbstractSequentialList<Directory>() {
			@Override
			public Iterator<Directory> iterator() {
				return directory.getDirectories(cache).iterator();
			}
		};
	}
}
