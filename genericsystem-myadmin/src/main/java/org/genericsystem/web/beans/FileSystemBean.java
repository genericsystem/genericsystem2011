package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.genericsystem.web.util.GsMessages;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

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

	public void changeFile(TreeSelectionChangeEvent selectionChangeEvent) {
		List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
		if (!selection.isEmpty()) {
			Object currentSelectionKey = selection.get(0);

			UITree tree = (UITree) selectionChangeEvent.getSource();
			Object storedKey = tree.getRowKey();
			tree.setRowKey(currentSelectionKey);
			selectedFile = (Generic) tree.getRowData();
			tree.setRowKey(storedKey);
			messages.info("selectionchanged", selectedFile.getValue());
		}
	}

	public void addRootDirectory(String newValue) {
		messages.info("createRootDirectory", newValue);
		cache.<FileSystem> find(FileSystem.class).addRootDirectory(cache, newValue);
	}

	public void addSubDirectory(String newValue) {
		messages.info("createSubDirectory", newValue, selectedFile.getValue());
		((Directory) selectedFile).addDirectory(cache, newValue);
	}

	public void addFile(String newValue) {
		messages.info("createFile", newValue, selectedFile.getValue());
		((Directory) selectedFile).addFile(cache, newValue);
	}

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	public void updateShortPath(ValueChangeEvent vce) {
		String shortPath = (String) vce.getNewValue();
		log.info("updateShortPath : " + shortPath);
		messages.info("updateShortPath", shortPath, selectedFile.getValue());
		selectedFile = ((CacheImpl) cache).update(selectedFile, shortPath);
	}

	public Wrapper getWrapper(Generic g) {
		assert g != null;
		return new Wrapper(g);
	}

	public class Wrapper {
		private Generic generic;

		public Wrapper(Generic g) {
			this.generic = g;
		}

		public String getShortPath() {
			return generic.getValue();
		}

		public void setShortPath(String newValue) {
			log.info("updateShortPath : " + newValue + " " + generic);
			messages.info("updateShortPath", newValue, generic);
			selectedFile = ((CacheImpl) cache).update(generic, newValue);
		}
	}

	public void delete() {
		messages.info("deleteFile", selectedFile.getValue());
		selectedFile.remove(cache);
		selectedFile = null;
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
	}

}
