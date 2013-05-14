package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = 5535643610156313741L;

	protected static Logger log = LoggerFactory.getLogger(FileSystemBean.class);

	@Inject
	private transient Cache cache;

	@Inject
	private Messages messages;

	private Generic fileSelected;

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
		Generic selected = (Generic) tree.getRowData();
		tree.setRowKey(storedKey);

		log.info("changeFile " + selected);
		messages.info(new BundleKey("/bundles/messages", "selectionchanged"), selected.getValue());

		setFileSelected(selected);
	}

	public void addRootDirectory(String newValue) {
		log.info("CREATE ROOT DIRECTORY " + newValue);
		FileSystem directoryTree = cache.<FileSystem> find(FileSystem.class);
		directoryTree.addRootDirectory(cache, newValue);
	}

	public void addSubDirectory(String newValue) {
		log.info("CREATE " + newValue + " of " + fileSelected);
		((Directory) fileSelected).addDirectory(cache, newValue);
	}

	public void addFile(String newValue) {
		log.info("CREATE " + newValue + " of " + fileSelected);
		((Directory) fileSelected).addFile(cache, newValue);
	}

	public void modifyValue(String newValue) {
		log.info("MODIFY VALUE OF " + fileSelected + ", NEW VALUE " + newValue + " " + cache);
		((CacheImpl) cache).update(fileSelected, newValue);
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

	public Generic getFileSelected() {
		return fileSelected;
	}

	public void setFileSelected(Generic fileSelected) {
		this.fileSelected = fileSelected;
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
