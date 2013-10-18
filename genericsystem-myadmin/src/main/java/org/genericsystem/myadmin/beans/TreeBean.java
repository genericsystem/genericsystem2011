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

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.util.GsMessages;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.InvokeApplication;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@Named
@SessionScoped
public class TreeBean implements Serializable {

	private static final long serialVersionUID = -1799171287514605774L;

	private TreeNode rootTreeNode;

	private TreeNode selectedTreeNode;

	private boolean implicitShow;

	//
	private boolean selectionLocked;

	@Inject
	private GsMessages messages;

	@Inject
	private Event<TreeSelectionEvent> launcher;
	//
	private TreeSelectionEvent event;

	//
	// @Inject
	// private Event<MenuEvent> menuEvent;
	//
	// @Inject
	// private Event<PanelTitleChangeEvent> panelTitleChangeEvent;
	//
	// private View currentView;
	//
	@Inject
	private GenericBean genericBean;

	//
	@PostConstruct
	public void init() {
		rootTreeNode = new TreeNode(null, genericBean.getGenericEdited(), GenericBean.DEFAULT_VIEW);
		selectedTreeNode = rootTreeNode;
	}

	public List<TreeNode> getChildrens(final TreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(implicitShow);
	}

	public GenericTreeNodeWrapper getGenericTreeNodeWrapper(TreeNode genericTreeNode) {
		return new GenericTreeNodeWrapper(genericTreeNode);
	}

	public class GenericTreeNodeWrapper {
		private TreeNode genericTreeNode;

		public GenericTreeNodeWrapper(TreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public String getValue() {
			return genericTreeNode.getValue();
		}

		public void setValue(String newValue) {
			Generic generic = genericTreeNode.getGeneric();
			if (!newValue.equals(generic.toString())) {
				genericTreeNode.setGeneric(generic.updateValue(newValue));
				messages.info("updateGeneric", newValue, generic.getValue());
				panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) genericTreeBean.getSelectedTreeNodeGeneric()).toCategoryString()));
			}
		}
	}

	//
	// public void changeView(@Observes ModifyViewEvent modifyViewEvent) {
	// currentView = modifyViewEvent.getView();
	// messages.info("showchanged", currentView);
	// }
	//
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

	// public TreeNode getSelectedTreeNode() {
	// return selectedTreeNode;
	// }
	//
	// void setSelectedTreeNode(TreeNode selectedTreeNode) {
	// this.selectedTreeNode = selectedTreeNode;
	// }

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

	//
	// @SuppressWarnings("unchecked")
	// public <T extends Generic> T getSelectedTreeNodeGeneric() {
	// return (T) selectedTreeNode.getGeneric();
	// }
	//
	// public String getSelectedTreeNodeValue() {
	// return selectedTreeNode.getValue();
	// }
	//
	public List<TreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	//
	// public void changeType(@Observes TreeSelectionEvent treeSelectionEvent) {
	// if (treeSelectionEvent.getId().equals("typestree")) {
	// selectedTreeNode = (TreeNode) treeSelectionEvent.getObject();
	// internalChangeType();
	// messages.info("typeselectionchanged", getSelectedTreeNodeGeneric().toString());
	// }
	// }
	//
	// /**
	// * Changes the title of panel typesmanager.
	// */
	// private void internalChangeType() {
	// menuEvent.fire(new MenuEvent(selectedTreeNode, implicitShow));
	// panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
	// }
	//
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

	//
	// // /**
	// // * Changes attribute of selected tree node.
	// // *
	// // * @param attributeIndex - index of attribute.
	// // */
	// // public void changeAttributeSelected(int attributeIndex) {
	// // Attribute attribute = (Attribute) selectedTreeNode.getChildrens(TreeType.ATTRIBUTES, implicitShow).get(attributeIndex).getGeneric();
	// // selectedTreeNode.setAttribute(attribute);
	// // selectedTreeNode.setTreeType(TreeType.VALUES);
	// // messages.info("showvalues", attribute);
	// // }
	//
	// // /**
	// // * Changes tree type.
	// // *
	// // * @param treeType - tree type.
	// // */
	// // public void changeTreeType(TreeType treeType) {
	// // selectedTreeNode.setTreeType(treeType);
	// // messages.info("showchanged", treeType);
	// // }
	//
	// /**
	// *
	// *
	// * @param generic
	// */
	// public void view(Generic generic) {
	// selectedTreeNode = changeView(rootTreeNode, generic);
	// internalChangeType();
	// messages.info("typeselectionchanged", selectedTreeNode.getGeneric());
	// }
	//
	// /**
	// * Retourns tree node ???
	// *
	// * @param genericTreeNode
	// * - tree node.
	// * @param generic
	// * - generic.
	// *
	// * @return
	// */
	// private TreeNode changeView(TreeNode genericTreeNode, Generic generic) {
	// if (genericTreeNode.getGeneric().equals(generic))
	// return genericTreeNode;
	// for (TreeNode tmp : getChildrens(genericTreeNode)) {
	// TreeNode child = changeView(tmp, generic);
	// if (child != null)
	// return child;
	// }
	// return null;
	// }
	//
	// /**
	// * Returns true if generic is value. False if not.
	// *
	// * @param generic
	// * - generic.
	// *
	// * @return true - if generic is value, false - if not.
	// */
	// TODO in GS CORE
	public boolean isValue(Generic generic) {
		return generic.isConcrete() && generic.isAttribute();
	}

	//
	// /**
	// * Returns CSS style of generic tree node.
	// *
	// * @param genericTreeNode
	// * - generic tree node.
	// *
	// * @return name of CSS style.
	// */
	public String getStyle(TreeNode genericTreeNode) {
		return genericTreeNode.isImplicitAutomatic(genericTreeNode.getGeneric()) || (isValue(genericTreeNode.getGeneric()) && !((Holder) genericTreeNode.getGeneric()).getBaseComponent().equals(getSelectedTreeNodeGeneric())) ? "implicitColor" : "";
	}

	public String getTypeIcon(TreeNode genericTreeNode) {
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

	public String getTypeIconTitle(TreeNode genericTreeNode) {
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

}
