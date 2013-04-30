package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ViewScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	@Inject
	private transient Cache cache;

	private Generic fileSelected;

	private boolean renderedEditFile;

	private String newFileName;

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
		Object currentSelectionKey = selection.get(0);

		UITree tree = (UITree) selectionChangeEvent.getSource();
		Object storedKey = tree.getRowKey();
		tree.setRowKey(currentSelectionKey);
		Generic currentSelection = (Generic) tree.getRowData();
		tree.setRowKey(storedKey);

		setFileSelected(currentSelection);
		renderedEditFile = true;
	}

	public void createFile() {
		log.info("CREATE " + newFileName + " of " + fileSelected);
		if (fileSelected instanceof Directory) {
			log.info("CREATE SUBDIRECTORY");
			Directory directory = (Directory) fileSelected;
			directory.addDirectory(cache, newFileName);
		}
	}

	public Generic getFileSelected() {
		return fileSelected;
	}

	public void setFileSelected(Generic fileSelected) {
		this.fileSelected = fileSelected;
	}

	public boolean isRenderedEditFile() {
		return renderedEditFile;
	}

	public void setRenderedEditFile(boolean renderedEditFile) {
		this.renderedEditFile = renderedEditFile;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

}
