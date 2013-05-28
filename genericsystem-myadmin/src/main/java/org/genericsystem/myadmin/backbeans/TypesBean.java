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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class TypesBean implements Serializable {

	private static final long serialVersionUID = 8042406937175946234L;

	// TODO clean
	private static Logger log = LoggerFactory.getLogger(TypesBean.class);

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	private GenericTreeNode selectedTreeNode;

	@Inject
	private Event<PanelSelectionEvent> selectedChanged;

	private GenericTreeNode rootTreeNode;

	@PostConstruct
	public void init() {
		rootTreeNode = new GenericTreeNode(null, cache.getEngine(), GenericTreeNode.TreeType_DEFAULT);
	}

	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	public List<GenericTreeNode> getChildrens(final GenericTreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(cache);
	}

	public void changeType(@Observes @TreeSelection TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			GenericTreeNode genericTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			selectedTreeNode = genericTreeNode;
			selectedChanged.fire(new PanelSelectionEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
			messages.info("selectionchanged", messages.getMessage("type"), getSelectedTreeNodeGeneric().toString());
		}
	}

	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);
		messages.info("showchanged", treeType);
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRoot", messages.getMessage("type"), newValue);
	}

	public void newSubType(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).newSubType(cache, newValue);
		messages.info("createSub", messages.getMessage("type"), newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public void addAttribute(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).addAttribute(cache, newValue);
		messages.info("createRoot", messages.getMessage("attribute"), newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public void newInstance(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).newInstance(cache, newValue);
		messages.info("createRoot", messages.getMessage("instance"), newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public String delete() {
		selectedTreeNode.getGeneric().remove(cache);
		selectedTreeNode = null;
		redirect.redirectInfo("deleteFile", getSelectedTreeNodeGeneric().getValue());
		return "HOME";
	}

	public boolean isTreeNodeSelected() {
		return selectedTreeNode == null;
	}

	public Generic getSelectedTreeNodeGeneric() {
		return selectedTreeNode.getGeneric();
	}

	public String getSelectedTreeNodeValue() {
		if (null == selectedTreeNode)
			return "";
		return selectedTreeNode.getValue();
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
				genericTreeNode.setGeneric(((CacheImpl) cache).update(generic, newValue));
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

	public String getIcon(GenericTreeNode genericTreeNode) {
		log.info("genericTreeNode " + genericTreeNode.getGeneric() + " " + genericTreeNode.getTreeType());
		switch (genericTreeNode.getTreeType()) {
		case INSTANCES:
			return messages.getInfos("down_green_arrow");
		case INHERITINGS:
			return messages.getInfos("down_right_green_arrow");
		case COMPONENTS:
			return messages.getInfos("");
		case COMPOSITES:
			return messages.getInfos("right_green_arrow");
		case ATTRIBUTES:
			return messages.getInfos("");
		case RELATIONS:
			return messages.getInfos("");
		}
		return messages.getInfos("down_green_arrow");
	}
}
