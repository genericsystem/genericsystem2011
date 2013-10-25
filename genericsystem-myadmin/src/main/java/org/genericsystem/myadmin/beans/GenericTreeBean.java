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
import org.genericsystem.myadmin.beans.MenuBean.MenuEvent;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.gui.GuiTreeNode;
import org.genericsystem.myadmin.gui.GuiTreeNode.TreeType;
import org.genericsystem.myadmin.util.GsMessages;
import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.InvokeApplication;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class GenericTreeBean implements Serializable {

	private static final long serialVersionUID = -1799171287514605774L;

	protected static Logger log = LoggerFactory.getLogger(GenericImpl.class);

	/* Injected beans */
	@Inject transient CacheProvider cacheProvider;
	@Inject private GsMessages gsMessages;

	private GuiTreeNode rootTreeNode;					// root node of the tree
	private GuiTreeNode selectedTreeNode;				// selected tree node
	private boolean selectionLocked;

	/* Events */
	@Inject private Event<MenuEvent> menuEvent;
	@Inject private Event<PanelTitleChangeEvent> panelTitleChangeEvent;
	private TreeSelectionEvent event;
	@Inject private Event<TreeSelectionEvent> launcher;

	/**
	 * Creates the root tree node and selects it.
	 */
	@PostConstruct
	public void init() {
		rootTreeNode = new GuiTreeNode(null,
				cacheProvider.getCurrentCache().getEngine());
		selectedTreeNode = rootTreeNode;
	}

	/**
	 * Returns the list with only root of tree node.
	 * 
	 * @return the list with only root of tree node.
	 */
	public List<GuiTreeNode> getRoots() {
		return Collections.singletonList(rootTreeNode);
	}

	/**
	 * Returns the list of children of tree node.
	 * 
	 * @param genericTreeNode - tree node.
	 * 
	 * @return list of children.
	 */
	//TODO: remove
	public List<GuiTreeNode> getNodeChildren(final GuiTreeNode genericTreeNode) {
		return genericTreeNode.getChildren();
	}

	/**
	 * Changes tree type.
	 * 
	 * @param treeType - tree type.
	 */
	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);

		gsMessages.info("showchanged", treeType);
	}

	/**
	 * 
	 * 
	 * @param generic
	 */
	public void view(Generic generic) {
		selectedTreeNode = changeView(rootTreeNode, generic);
		internalChangeType();

		gsMessages.info("typeselectionchanged", selectedTreeNode.getGeneric());
	}

	/**
	 * Returns the GUI tree node of supplied generic. Method takes in consideration the implicit
	 * show flag.
	 * 
	 * @param guiTreeNode - node of GUI tree.
	 * @param generic - generic.
	 * 
	 * @return
	 */
	// TODO: move to GuiTreeNode
	private GuiTreeNode changeView(GuiTreeNode guiTreeNode, Generic generic) {
		if (guiTreeNode.getGeneric().equals(generic))
			return guiTreeNode;
		for (GuiTreeNode child : guiTreeNode.getChildren()) {
			GuiTreeNode childTreeNode = changeView(child, generic);
			if (childTreeNode != null)
				return childTreeNode;
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
	 * Returns icon's name of tree node.
	 * 
	 * @param genericTreeNode - generic tree node.
	 * 
	 * @return name of icon.
	 */
	public String getTypeIcon(GuiTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta()) {
			if (generic.isType())
				return gsMessages.getInfos("bullet_square_red");
			if (generic.isReallyAttribute())
				return gsMessages.getInfos("bullet_triangle_red");
			if (generic.isRelation())
				return gsMessages.getInfos("bullet_ball_red");
		} else if (generic.isStructural()) {
			if (generic.isType())
				return gsMessages.getInfos("bullet_square_yellow");
			if (generic.isReallyAttribute())
				return gsMessages.getInfos("bullet_triangle_yellow");
			if (generic.isRelation())
				return gsMessages.getInfos("bullet_ball_yellow");
		} else if (generic.isConcrete()) {
			if (generic.isType())
				return gsMessages.getInfos("bullet_square_green");
			if (generic.isReallyAttribute())
				return gsMessages.getInfos("bullet_triangle_green");
			if (generic.isRelation())
				return gsMessages.getInfos("bullet_ball_green");
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
	public String getTypeIconTitle(GuiTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta() && generic.isType())
			return gsMessages.getMessage("meta") + " " + gsMessages.getMessage("type");
		else if (generic.isMeta() && generic.isReallyAttribute())
			return gsMessages.getMessage("meta") + " " + gsMessages.getMessage("attribute");
		else if (generic.isMeta() && generic.isRelation())
			return gsMessages.getMessage("meta") + " " + gsMessages.getMessage("relation");
		else if (generic.isStructural() && generic.isType())
			return gsMessages.getMessage("type");
		else if (generic.isStructural() && generic.isReallyAttribute())
			return gsMessages.getMessage("attribute");
		else if (generic.isStructural() && generic.isRelation())
			return gsMessages.getMessage("relation");
		else if (generic.isConcrete() && generic.isType())
			return gsMessages.getMessage("instance");
		else if (generic.isConcrete() && generic.isReallyAttribute())
			return gsMessages.getMessage("value");
		else if (generic.isConcrete() && generic.isRelation())
			return gsMessages.getMessage("link");
		throw new IllegalStateException();
	}

	/**
	 * Change the selected node in the tree of generics.
	 * 
	 * @param selectionChangeEvent - JSF event of selection change.
	 */
	public void changeSelectedNode(TreeSelectionChangeEvent treeSelectionChangeEvent) {
		if (!selectionLocked) {
			List<Object> selection = new ArrayList<Object>(treeSelectionChangeEvent.getNewSelection());
			if (!selection.isEmpty()) {
				Object currentSelectionKey = selection.get(0);
				UITree tree = (UITree) treeSelectionChangeEvent.getSource();
				Object storedKey = tree.getRowKey();
				tree.setRowKey(currentSelectionKey);
				event = new TreeSelectionEvent(tree.getId(), tree.getRowData());
				tree.setRowKey(storedKey);
			}
		}
	}

	/**
	 * Changes the type of something. ???
	 * 
	 * @param treeSelectionEvent
	 */
	public void changeType(@Observes TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			selectedTreeNode = (GuiTreeNode) treeSelectionEvent.getObject();
			internalChangeType();
			gsMessages.info("typeselectionchanged", getSelectedTreeNode().getGeneric().toString());
		}
	}

	/**
	 * Changes the title of panel typesmanager.
	 */
	private void internalChangeType() {
		menuEvent.fire(new MenuEvent(selectedTreeNode));
		panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNode().getGeneric()).toCategoryString()));
	}

	/**
	 * Fires event of tree selection after phase INVOKE_APPLICATION.
	 * 
	 * @param phaseEvent - event of phase changing.
	 */
	public void fireEvent(@Observes @InvokeApplication @After PhaseEvent phaseEvent) {
		if (event != null) {
			launcher.fire(event);
			event = null;
		}
	}

	/**
	 * Returns selected tree node.
	 * 
	 * @return selected tree node.
	 */
	public GuiTreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	/**
	 * Sets selected tree node.
	 * 
	 * @param selectedTreeNode - selected tree node.
	 */
	void setSelectedTreeNode(GuiTreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
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

}
