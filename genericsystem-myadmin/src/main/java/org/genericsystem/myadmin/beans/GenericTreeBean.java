package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.myadmin.beans.GenericTreeNode.TreeType;
import org.genericsystem.myadmin.beans.MenuBean.MenuEvent;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.util.GsMessages;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.InvokeApplication;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@Named
@SessionScoped
public class GenericTreeBean implements Serializable {

	private static final long serialVersionUID = -1799171287514605774L;

	private GenericTreeNode rootTreeNode;

	private GenericTreeNode selectedTreeNode;

	@Inject
	private transient Cache cache;

	private boolean implicitShow;

	private boolean selectionLocked;

	@Inject
	private GsMessages messages;

	@Inject
	// @TreeSelection
	private Event<TreeSelectionEvent> launcher;

	private TreeSelectionEvent event;

	@Inject
	private Event<MenuEvent> menuEvent;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	@PostConstruct
	public void init() {
		rootTreeNode = new GenericTreeNode(null, cache.getEngine(), GenericTreeNode.TreeType_DEFAULT);
		selectedTreeNode = rootTreeNode;
	}

	public GenericTreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	void setSelectedTreeNode(GenericTreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	public boolean isImplicitShow() {
		return implicitShow;
	}

	public void setImplicitShow(boolean implicitShow) {
		this.implicitShow = implicitShow;
	}

	public boolean isSelectionLocked() {
		return selectionLocked;
	}

	public void setSelectionLocked(boolean selectionLocked) {
		this.selectionLocked = selectionLocked;
	}

	Generic getSelectedTreeNodeGeneric() {
		return selectedTreeNode.getGeneric();
	}

	public String getSelectedTreeNodeValue() {
		return selectedTreeNode.getValue();
	}

	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	public List<GenericTreeNode> getChildrens(final GenericTreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(implicitShow);
	}

	public void changeType(@Observes/* @TreeSelection */TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			selectedTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			internalChangeType();
			messages.info("typeselectionchanged", getSelectedTreeNodeGeneric().toString());
		}
	}

	private void internalChangeType() {
		menuEvent.fire(new MenuEvent(selectedTreeNode, implicitShow));
		panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
	}

	public void change(TreeSelectionChangeEvent selectionChangeEvent) {
		if (!selectionLocked) {
			List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
			if (!selection.isEmpty()) {
				Object currentSelectionKey = selection.get(0);
				UITree tree = (UITree) selectionChangeEvent.getSource();
				Object storedKey = tree.getRowKey();
				tree.setRowKey(currentSelectionKey);
				event = new TreeSelectionEvent(tree.getId(), tree.getRowData());
				tree.setRowKey(storedKey);
			}
		}
	}

	public void fire(@Observes @InvokeApplication @After PhaseEvent e) {
		if (event != null) {
			launcher.fire(event);
			event = null;
		}
	}

	public void changeAttributeSelected(int attributeIndex) {
		Attribute attribute = (Attribute) selectedTreeNode.getChildrens(TreeType.ATTRIBUTES, implicitShow).get(attributeIndex).getGeneric();
		selectedTreeNode.setAttribute(attribute);
		selectedTreeNode.setTreeType(TreeType.VALUES);
		messages.info("showvalues", attribute);
	}

	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);
		messages.info("showchanged", treeType);
	}

	public void view(Generic generic) {
		selectedTreeNode = changeView(rootTreeNode, generic);
		internalChangeType();
		messages.info("typeselectionchanged", selectedTreeNode.getGeneric());
	}

	private GenericTreeNode changeView(GenericTreeNode genericTreeNode, Generic generic) {
		if (genericTreeNode.getGeneric().equals(generic))
			return genericTreeNode;
		for (GenericTreeNode tmp : getChildrens(genericTreeNode)) {
			GenericTreeNode child = changeView(tmp, generic);
			if (null != child)
				return child;
		}
		return null;
	}

	public boolean isTreeTypeSelected(TreeType treeType) {
		return selectedTreeNode != null && selectedTreeNode.getTreeType() == treeType;
	}

	// TODO in GS CORE
	public boolean isValue(Generic generic) {
		return generic.isConcrete() && generic.isAttribute();
	}

	public String getStyle(GenericTreeNode genericTreeNode) {
		return genericTreeNode.isImplicitAutomatic(genericTreeNode.getGeneric()) || (isValue(genericTreeNode.getGeneric()) && !((Holder) genericTreeNode.getGeneric()).getBaseComponent().equals(getSelectedTreeNodeGeneric())) ? "implicitColor" : "";
	}

	// TODO no more used
	// public String getExpandedIcon() {
	// return getExpandedIcon(selectedTreeNode);
	// }

	// TODO no more used
	// public String getExpandedIcon(GenericTreeNode genericTreeNode) {
	// switch (genericTreeNode.getTreeType()) {
	// case SUPERS:
	// return messages.getInfos("up_green_arrow");
	// case INSTANCES:
	// return messages.getInfos("down_green_arrow");
	// case INHERITINGS:
	// return messages.getInfos("down_right_green_arrow");
	// case COMPONENTS:
	// return messages.getInfos("left_green_arrow");
	// case COMPOSITES:
	// return messages.getInfos("right_green_arrow");
	// case ATTRIBUTES:
	// return messages.getInfos("up_right_green_arrow");
	// case VALUES:
	// return messages.getInfos("right_green_arrow");
	// default:
	// break;
	// }
	// throw new IllegalStateException();
	// }

	// TODO no more used
	// public String getCollapsedIcon() {
	// return getCollapsedIcon(selectedTreeNode);
	// }

	// TODO no more used
	// public String getCollapsedIcon(GenericTreeNode genericTreeNode) {
	// switch (genericTreeNode.getTreeType()) {
	// case SUPERS:
	// return messages.getInfos("up_red_arrow");
	// case INSTANCES:
	// return messages.getInfos("down_red_arrow");
	// case INHERITINGS:
	// return messages.getInfos("down_right_red_arrow");
	// case COMPONENTS:
	// return messages.getInfos("left_red_arrow");
	// case COMPOSITES:
	// return messages.getInfos("right_red_arrow");
	// case ATTRIBUTES:
	// return messages.getInfos("up_right_red_arrow");
	// case VALUES:
	// return messages.getInfos("right_red_arrow");
	// default:
	// break;
	// }
	// throw new IllegalStateException();
	// }

	// TODO no more used
	// public String getIconTitle() {
	// return getIconTitle(selectedTreeNode);
	// }

	// TODO no more used
	// public String getIconTitle(GenericTreeNode genericTreeNode) {
	// switch (genericTreeNode.getTreeType()) {
	// case SUPERS:
	// return messages.getMessage("super");
	// case INSTANCES:
	// return messages.getMessage("instance");
	// case INHERITINGS:
	// return messages.getMessage("inheriting");
	// case COMPONENTS:
	// return messages.getMessage("component");
	// case COMPOSITES:
	// return messages.getMessage("composite");
	// case ATTRIBUTES:
	// return messages.getMessage("attribute");
	// case VALUES:
	// return messages.getMessage("value");
	// default:
	// break;
	// }
	// throw new IllegalStateException();
	// }

	public String getTypeIcon(GenericTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_red");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_red");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_red");
		} else if (generic.isStructural()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_yellow");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_yellow");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_yellow");
		} else if (generic.isConcrete()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_green");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_green");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_green");
		}
		throw new IllegalStateException();
	}

	public String getTypeIconTitle(GenericTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta() && generic.isType())
			return messages.getMessage("meta") + " " + messages.getMessage("type");
		else if (generic.isMeta() && generic.isReallyAttribute())
			return messages.getMessage("meta") + " " + messages.getMessage("attribute");
		else if (generic.isMeta() && generic.isRelation())
			return messages.getMessage("meta") + " " + messages.getMessage("relation");
		else if (generic.isStructural() && generic.isType())
			return messages.getMessage("type");
		else if (generic.isStructural() && generic.isReallyAttribute())
			return messages.getMessage("attribute");
		else if (generic.isStructural() && generic.isRelation())
			return messages.getMessage("relation");
		else if (generic.isConcrete() && generic.isType())
			return messages.getMessage("instance");
		else if (generic.isConcrete() && generic.isReallyAttribute())
			return messages.getMessage("value");
		else if (generic.isConcrete() && generic.isRelation())
			return messages.getMessage("link");
		throw new IllegalStateException();
	}

	public GenericTreeNodeWrapper getGenericTreeNodeWrapper(GenericTreeNode genericTreeNode) {
		return new GenericTreeNodeWrapper(genericTreeNode);
	}

	public static class TreeSelectionEvent {
		private final String id;
		private final Object object;

		public TreeSelectionEvent(String id, Object object) {
			this.id = id;
			this.object = object;
		}

		public String getId() {
			return id;
		}

		public Object getObject() {
			return object;
		}

	}

	public class GenericTreeNodeWrapper {
		private GenericTreeNode genericTreeNode;

		public GenericTreeNodeWrapper(GenericTreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public String getValue() {
			return genericTreeNode.getValue();
		}

		public void setValue(String newValue) {
			Generic generic = genericTreeNode.getGeneric();
			if (!newValue.equals(generic.toString())) {
				genericTreeNode.setGeneric(generic.updateKey(newValue));
				messages.info("updateGeneric", newValue, generic.getValue());
				panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
			}
		}
	}

}
