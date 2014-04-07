package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Generic;
import org.genericsystem.file.FileSystem;
import org.genericsystem.file.FileSystem.Directory;
import org.genericsystem.file.FileSystem.FileType.File;
import org.genericsystem.jsf.util.GsMessages;
import org.genericsystem.jsf.util.GsRedirect;
import org.genericsystem.myadmin.beans.GuiGenericsTreeBean.TreeSelectionEvent;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;

@Named
@SessionScoped
public class FileSystemBean implements Serializable {

	private static final long serialVersionUID = -646738653059697257L;

	@Inject
	transient CacheProvider cacheProvider;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	private Generic selectedFile;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	private String name;

	public List<Directory> getRootDirectories() {
		return cacheProvider.getCurrentCache().<FileSystem> find(FileSystem.class).getRootDirectories();
	}

	public List<Directory> getDirectories(final Directory directory) {
		return directory.getDirectories();
	}

	public List<File> getFiles(final Directory directory) {
		return directory.getFiles();
	}

	public void changeFile(@Observes TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("directorytree")) {
			selectedFile = (Generic) treeSelectionEvent.getObject();
			panelTitleChangeEvent.fire(new PanelTitleChangeEvent("filesystemmanager", getShortPath()));
			messages.info(isFileSelected() ? "fileselectionchanged" : "directoryselectionchanged", selectedFile.getValue());
		}
	}

	public void addRootDirectory() {
		cacheProvider.getCurrentCache().<FileSystem> find(FileSystem.class).addRootDirectory(name);
		messages.info("createRootDirectory", name);
	}

	public void addSubDirectory() {
		((Directory) selectedFile).addDirectory(name);
		messages.info("createSubDirectory", name, selectedFile.getValue());
	}

	public void addFile() {
		((Directory) selectedFile).addFile(name);
		messages.info("createFile", name);
	}

	public Wrapper getWrapper(Generic generic) {
		return new Wrapper(generic);
	}

	public Wrapper getWrapper() {
		return new Wrapper(selectedFile);
	}

	public class Wrapper {
		private final Generic generic;

		public Wrapper(Generic generic) {
			this.generic = generic;
		}

		public String getShortPath() {
			return generic.getValue();
		}

		public void setShortPath(String newValue) {
			if (!newValue.equals(generic.getValue())) {
				selectedFile = generic.setValue(newValue);
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

	public String delete() {
		selectedFile.remove();
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
		return Objects.toString(selectedFile);
	}

	public String getContent() {
		if (selectedFile == null)
			return "";
		byte[] bytes = ((File) selectedFile).getContent();
		return new String(bytes != null ? new String(bytes) : "");
	}

	public void setContent(String content) {
		((File) selectedFile).setContent(content.getBytes());
		messages.info("setContent", selectedFile.getValue());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
