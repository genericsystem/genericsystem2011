package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
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

	private String newRootDirectory;

	private String newSubDirectoryName;

	private String newFileName;

	private String newValue;

	// private String content;

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

		log.info("changeFile " + currentSelection);
		setFileSelected(currentSelection);
	}

	public void addRootDirectory() {
		log.info("CREATE ROOT DIRECTORY " + newRootDirectory);
		FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);
		directoryTree.addRootDirectory(cache, newRootDirectory);
		newRootDirectory = "";
	}

	public void addSubDirectory() {
		log.info("CREATE " + newSubDirectoryName + " of " + fileSelected);
		((Directory) fileSelected).addDirectory(cache, newSubDirectoryName);
		newSubDirectoryName = "";
	}

	public void addFile() {
		log.info("CREATE " + newFileName + " of " + fileSelected);
		((Directory) fileSelected).addFile(cache, newFileName);
		newFileName = "";
	}

	public void modifyValue() {
		log.info("MODIFY VALUE OF " + fileSelected + ", NEW VALUE " + newValue + " " + cache);
		((CacheImpl) cache).update(fileSelected, newValue);
		newValue = "";
	}

	public void delete() {
		fileSelected.remove(cache);
	}

	public boolean isDirectorySelected() {
		boolean check = fileSelected != null && fileSelected instanceof Directory;
		log.info("isDirectory " + fileSelected + " " + check);
		return check;
	}

	public boolean isFileSelected() {
		boolean check = fileSelected != null && fileSelected instanceof File;
		log.info("isDirectory " + fileSelected + " " + check);
		return check;
	}

	public void modifyValueWindow() {

	}

	public Generic getFileSelected() {
		return fileSelected;
	}

	public void setFileSelected(Generic fileSelected) {
		this.fileSelected = fileSelected;
	}

	public String getNewRootDirectory() {
		return newRootDirectory;
	}

	public void setNewRootDirectory(String newRootDirectory) {
		this.newRootDirectory = newRootDirectory;
	}

	public String getNewSubDirectoryName() {
		return newSubDirectoryName;
	}

	public void setNewSubDirectoryName(String newSubDirectoryName) {
		this.newSubDirectoryName = newSubDirectoryName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getFileShortPath() {
		return ((File) fileSelected).getShortPath();
	}

	public String getDirectoryShortPath() {
		return ((Directory) fileSelected).getShortPath();
	}

	public String getContent() {
		if (fileSelected == null)
			return "";
		byte[] bytes = ((File) fileSelected).getContent(cache);
		return new String(bytes != null ? new String(bytes) : "");
	}

	public void setContent(String content) {
		((File) fileSelected).setContent(cache, content.getBytes());
	}

}
