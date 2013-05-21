package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.genericsystem.myadmin.beans.qualifier.TreeSelection;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.myadmin.util.GsRedirect;

@Named
@SessionScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	GsRedirect redirect;

	private Generic selectedFile;

	public List<Directory> getRootDirectories() {
		return cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).toList();
	}

	public List<Directory> getDirectories(final Directory directory) {
		return directory.getDirectories(cache).toList();
	}

	public List<File> getFiles(final Directory directory) {
		return directory.getFiles(cache).toList();
	}

	public void changeFile(@Observes @TreeSelection Generic generic) {
		selectedFile = generic;
		messages.info("selectionchanged", isFileSelected() ? messages.getMessage("file") : messages.getMessage("directory"), selectedFile.getValue());
	}

	public void addRootDirectory(String newValue) {
		cache.<FileSystem> find(FileSystem.class).addRootDirectory(cache, newValue);
		messages.info("createRootDirectory", newValue);
	}

	public void addSubDirectory(String newValue) {
		((Directory) selectedFile).addDirectory(cache, newValue);
		messages.info("createSubDirectory", newValue, selectedFile.getValue());
	}

	public void addFile(String newValue) {
		((Directory) selectedFile).addFile(cache, newValue);
		messages.info("createFile", newValue, selectedFile.getValue());
	}

	public Wrapper getWrapper(Generic generic) {
		return new Wrapper(generic);
	}

	public class Wrapper {
		private Generic generic;

		public Wrapper(Generic generic) {
			this.generic = generic;
		}

		public String getShortPath() {
			return generic.getValue();
		}

		public void setShortPath(String newValue) {
			if (!newValue.equals(generic.getValue())) {
				selectedFile = ((CacheImpl) cache).update(generic, newValue);
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

	public String delete() {
		selectedFile.remove(cache);
		redirect.redirectInfo("deleteFile", selectedFile.getValue());
		selectedFile = null;
		return "HOME";
	}

	public boolean isDirectorySelected() {
		return selectedFile != null && selectedFile instanceof Directory;
	}

	public boolean isFileSelected() {
		return selectedFile != null && selectedFile instanceof File;
	}

	public String getFileShortPath() {
		return ((File) selectedFile).getShortPath();
	}

	public String getDirectoryShortPath() {
		return ((Directory) selectedFile).getShortPath();
	}

	public String getContent() {
		if (selectedFile == null)
			return "";
		byte[] bytes = ((File) selectedFile).getContent(cache);
		return new String(bytes != null ? new String(bytes) : "");
	}

	public void setContent(String content) {
		((File) selectedFile).setContent(cache, content.getBytes());
		redirect.redirectInfo("setContent", selectedFile.getValue());
	}
}
