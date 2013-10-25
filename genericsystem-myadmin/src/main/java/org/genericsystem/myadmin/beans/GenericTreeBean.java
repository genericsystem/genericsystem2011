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

import org.genericsystem.cdi.CacheProvider;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Holder;
import org.genericsystem.myadmin.beans.MenuBean.MenuEvent;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.gui.GenericTreeNode;
import org.genericsystem.myadmin.gui.GenericTreeNode.TreeType;
import org.genericsystem.myadmin.util.GsMessages;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.InvokeApplication;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@Named
@SessionScoped
public class GenericTreeBean implements Serializable {

	private static final long serialVersionUID = -1799171287514605774L;

	private GenericTreeNode rootTreeNode;					// root node of the tree
	private GenericTreeNode selectedTreeNode;				// selected tree node

	@Inject transient CacheProvider cacheProvider;

	private boolean implicitShow;

	private boolean selectionLocked;

	@Inject
	private GsMessages messages;

	@Inject
	private Event<TreeSelectionEvent> launcher;

	private TreeSelectionEvent event;

	@Inject
	private Event<MenuEvent> menuEvent;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	/**
	 * Event of selection of node of the tree.
	 * 
	 * @author middleware
	 */
	public static class TreeSelectionEvent {

		private final String id;						// id of event
		private final Object object;					// concerned object

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

	/**
	 * Creates the root tree node and selects it.
	 */
	@PostConstruct
	public void init() {
		rootTreeNode = new GenericTreeNode(null, cacheProvider.getCurrentCache().getEngine(), GenericTreeNode.TreeType_DEFAULT);
		selectedTreeNode = rootTreeNode;
	}

	/**
	 * Returns selected tree node.
	 * 
	 * @return selected tree node.
	 */
	public GenericTreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	/**
	 * Sets selected tree node.
	 * 
	 * @param selectedTreeNode - selected tree node.
	 */
	void setSelectedTreeNode(GenericTreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	/**
	 * Return value of flag implicit show.
	 * 
	 * @return value of flag implicit show.
	 */
	public boolean isImplicitShow() {
		return implicitShow;
	}

	/**
	 * Sets flag implicit show.
	 * 
	 * @param implicitShow - implicit show flag.
	 */
	public void setImplicitShow(boolean implicitShow) {
		this.implicitShow = implicitShow;
	}

	/**
	 * Returns value of flag selection locked.
	 * 
	 * @return value of flag selection locked.
	 */
	public boolean isSelectionLocked() {
		return selectionLocked;
	}

	/**
	 * Sets flag selection locked.
	 * 
	 * @param selectionLocked - flag selection locked.
	 */
	public void setSelectionLocked(boolean selectionLocked) {
		this.selectionLocked = selectionLocked;
	}

	/**
	 * Returns generic of selected tree node.
	 * 
	 * @return generic of selected tree node.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Generic> T getSelectedTreeNodeGeneric() {
		return (T) selectedTreeNode.getGeneric();
	}

	/**
	 * Returns the value of selected tree node.
	 * 
	 * @return the value of selected tree node.
	 */
	public String getSelectedTreeNodeValue() {
		return selectedTreeNode.getValue();
	}

	/**
	 * Returns the list with only root of tree node.
	 * 
	 * @return the list with only root of tree node.
	 */
	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	/**
	 * Returns the list of children of tree node.
	 * 
	 * @param genericTreeNode - tree node.
	 * 
	 * @return list of children.
	 */
	public List<GenericTreeNode> getChildrens(final GenericTreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(implicitShow);
	}

	/**
	 * Changes the type of something. ???
	 * 
	 * @param treeSelectionEvent
	 */
	public void changeType(@Observes TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			selectedTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			internalChangeType();
			messages.info("typeselectionchanged", getSelectedTreeNodeGeneric().toString());
		}
	}

	/**
	 * Changes the title of panel typesmanager.
	 */
	private void internalChangeType() {
		menuEvent.fire(new MenuEvent(selectedTreeNode, implicitShow));
		panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
	}

	/**
	 * ???
	 * 
	 * @param selectionChangeEvent
	 */
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

	/**
	 * Fires event.
	 * 
	 * @param e - phase of event.
	 */
	public void fire(@Observes @InvokeApplication @After PhaseEvent e) {
		if (event != null) {
			launcher.fire(event);
			event = null;
		}
	}

	//	/**
	//	 * Changes attribute of selected tree node.
	//	 *
	//	 * @param attributeIndex - index of attribute.
	//	 */
	//	public void changeAttributeSelected(int attributeIndex) {
	//		Attribute attribute = (Attribute) selectedTreeNode.getChildrens(TreeType.ATTRIBUTES, implicitShow).get(attributeIndex).getGeneric();
	//		selectedTreeNode.setAttribute(attribute);
	//		selectedTreeNode.setTreeType(TreeType.VALUES);
	//		messages.info("showvalues", attribute);
	//	}

	/**
	 * Changes tree type.
	 * 
	 * @param treeType - tree type.
	 */
	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);
		messages.info("showchanged", treeType);
	}

	/**
	 * 
	 * 
	 * @param generic
	 */
	public void view(Generic generic) {
		selectedTreeNode = changeView(rootTreeNode, generic);
		internalChangeType();
		messages.info("typeselectionchanged", selectedTreeNode.getGeneric());
	}

	/**
	 * Retourns tree node ???
	 * 
	 * @param genericTreeNode - tree node.
	 * @param generic - generic.
	 * 
	 * @return
	 */
	private GenericTreeNode changeView(GenericTreeNode genericTreeNode, Generic generic) {
		if (genericTreeNode.getGeneric().equals(generic))
			return genericTreeNode;
		for (GenericTreeNode tmp : getChildrens(genericTreeNode)) {
			GenericTreeNode child = changeView(tmp, generic);
			if (child != null)
				return child;
		}
		return null;
	}

	/**
	 * Returns true if tree type of selected tree node is equal to type in parameters. False if not.
	 * 
	 * @param treeType - tree type.
	 * 
	 * @return true - if tree type of selected tree node is equal to type in parameters,
	 * false - if not.
	 */
	public boolean isTreeTypeSelected(TreeType treeType) {
		return selectedTreeNode != null && selectedTreeNode.getTreeType() == treeType;
	}

	/**
	 * Returns true if generic is value. False if not.
	 * 
	 * @param generic - generic.
	 * 
	 * @return true - if generic is value, false - if not.
	 */
	// TODO in GS CORE
	public boolean isValue(Generic generic) {
		return generic.isConcrete() && generic.isAttribute();
	}

	/**
	 * Returns CSS style of generic tree node.
	 * 
	 * @param genericTreeNode - generic tree node.
	 * 
	 * @return name of CSS style.
	 */
	public String getStyle(GenericTreeNode genericTreeNode) {
		return genericTreeNode.isImplicitAutomatic(
				genericTreeNode.getGeneric()) ||
				(isValue(genericTreeNode.getGeneric()) &&
						!((Holder) genericTreeNode.getGeneric()).getBaseComponent().equals(getSelectedTreeNodeGeneric())
						) ? "implicitColor" : "";
	}

	/**
	 * Returns icon's name of tree node.
	 * 
	 * @param genericTreeNode - generic tree node.
	 * 
	 * @return name of icon.
	 */
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

	/**
	 * Returns the title of icone.
	 * 
	 * @param genericTreeNode - generic tree node.
	 * 
	 * @return title of icon.
	 */
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

}
