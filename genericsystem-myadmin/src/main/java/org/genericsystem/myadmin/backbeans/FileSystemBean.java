package org.genericsystem.myadmin.backbeans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.genericsystem.myadmin.beans.PanelBean.PanelSelectionEvent;
import org.genericsystem.myadmin.beans.TreeBean.TreeSelectionEvent;
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

	@Inject
	private Event<PanelSelectionEvent> selectedChanged;

	public List<Directory> getRootDirectories() {
		return cache.<FileSystem> find(FileSystem.class).getRootDirectories(cache).toList();
	}

	public List<Directory> getDirectories(final Directory directory) {
		return directory.getDirectories(cache).toList();
	}

	public List<File> getFiles(final Directory directory) {
		return directory.getFiles(cache).toList();
	}

	public void changeFile(@Observes @TreeSelection TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("directorytree")) {
			selectedFile = (Generic) treeSelectionEvent.getObject();
			selectedChanged.fire(new PanelSelectionEvent("filesystemmanager", getShortPath()));
			messages.info("selectionchanged", isFileSelected() ? messages.getMessage("file") : messages.getMessage("directory"), selectedFile.getValue());
		}
	}

	public void addRootDirectory(String newValue) {
		cache.<FileSystem> find(FileSystem.class).addRootDirectory(cache, newValue);
		messages.info("createRoot", messages.getMessage("directory"), newValue);
	}

	public void addSubDirectory(String newValue) {
		((Directory) selectedFile).addDirectory(cache, newValue);
		messages.info("createSub", messages.getMessage("directory"), newValue, selectedFile.getValue());
	}

	public void addFile(String newValue) {
		((Directory) selectedFile).addFile(cache, newValue);
		messages.info("createFile", newValue, selectedFile.getValue());
		messages.info("createRoot", messages.getMessage("file"), newValue);
	}

	public Wrapper getWrapper(Generic generic) {
		return new Wrapper(generic);
	}

	public Wrapper getWrapper() {
		return new Wrapper(selectedFile);
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

	public String getShortPath() {
		return isFileSelected() ? ((File) selectedFile).toCategoryString() : ((Directory) selectedFile).toCategoryString();
	}

	@Override
	public String toString() {
		if (null == selectedFile)
			return "";
		return selectedFile.toString();
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
