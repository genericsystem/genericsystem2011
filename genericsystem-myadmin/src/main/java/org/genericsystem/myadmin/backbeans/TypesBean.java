package org.genericsystem.myadmin.backbeans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.backbeans.GenericTreeNode.TreeType;
import org.genericsystem.myadmin.beans.PanelBean.PanelSelectionEvent;
import org.genericsystem.myadmin.beans.TreeBean.TreeSelectionEvent;
import org.genericsystem.myadmin.beans.qualifier.TreeSelection;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.myadmin.util.GsRedirect;

@Named
@SessionScoped
public class TypesBean implements Serializable {

	private static final long serialVersionUID = 8042406937175946234L;

	// TODO clean
	// private static Logger log = LoggerFactory.getLogger(TypesBean.class);

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	private Generic selectedType;

	private TreeType selectedTreeType;

	@Inject
	private Event<PanelSelectionEvent> selectedChanged;

	private List<GenericTreeNode> roots;

	@PostConstruct
	public void init() {
		roots = Collections.singletonList(new GenericTreeNode(cache.getEngine()));
	}

	public List<GenericTreeNode> getRoot() {
		return roots;
	}

	public WrapperTreeType getTreeType(GenericTreeNode genericTreeNode) {
		return new WrapperTreeType(genericTreeNode.getTreeType(genericTreeNode.getGeneric()).name());
	}

	public class WrapperTreeType {

		public WrapperTreeType(String treeType) {
			this.treeType = treeType;
		}

		private String treeType;

		public String getTreeType() {
			return treeType;
		}

		public void setTreeType(String treeType) {
			this.treeType = treeType;
		}
	}

	public List<GenericTreeNode> getDirectSubTypes(final GenericTreeNode genericTreeNode) {
		genericTreeNode.setTreeType(selectedType, selectedTreeType);
		return genericTreeNode.getChildrens(cache);
	}

	public void changeType(@Observes @TreeSelection TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			GenericTreeNode genericTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			selectedType = genericTreeNode.getGeneric();
			selectedTreeType = null;
			selectedChanged.fire(new PanelSelectionEvent("typesmanager", getSelectedType().toCategoryString()));
			messages.info("selectionchanged", messages.getMessage("type"), selectedType.toString());
		}
	}

	public void changeTreeType(TreeType treeType) {
		selectedTreeType = treeType;
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRoot", messages.getMessage("type"), newValue);
	}

	public void newSubType(String newValue) {
		((Type) selectedType).newSubType(cache, newValue);
		messages.info("createSub", messages.getMessage("type"), newValue, selectedType.getValue());
	}

	public void addAttribute(String newValue) {
		((Type) selectedType).addAttribute(cache, newValue);
		messages.info("createRoot", messages.getMessage("attribute"), newValue, selectedType.getValue());
	}

	public void newInstance(String newValue) {
		((Type) selectedType).newInstance(cache, newValue);
		messages.info("createRoot", messages.getMessage("instance"), newValue, selectedType.getValue());
	}

	public String delete() {
		selectedType.remove(cache);
		selectedType = null;
		redirect.redirectInfo("deleteFile", selectedType.getValue());
		return "HOME";
	}

	public boolean isType() {
		return selectedType != null && selectedType.isType();
	}

	public boolean isInstance() {
		// TODO in core
		return selectedType != null && selectedType.getComponentsSize() == 0 && selectedType.isConcrete();
	}

	public GenericImpl getSelectedType() {
		return (GenericImpl) selectedType;
	}

	public void setSelectedType(Generic selectedType) {
		this.selectedType = selectedType;
	}

	public String getSelectedTypeValue() {
		if (null == selectedType)
			return "";
		return selectedType.toString();
	}

	public Wrapper getWrapper(GenericTreeNode genericTreeNode) {
		return new Wrapper(genericTreeNode);
	}

	public class Wrapper {
		private GenericTreeNode genericTreeNode;

		public Wrapper(GenericTreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public String getValue() {
			return genericTreeNode.getValue();
		}

		public void setValue(String newValue) {
			Generic generic = genericTreeNode.getGeneric();
			if (!newValue.equals(generic.toString())) {
				selectedType = ((CacheImpl) cache).update(generic, newValue);
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}
}
