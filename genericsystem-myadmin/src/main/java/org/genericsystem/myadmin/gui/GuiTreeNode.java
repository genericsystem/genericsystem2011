package org.genericsystem.myadmin.gui;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;

public class GuiTreeNode {

	// protected static Logger log = LoggerFactory.getLogger(GenericTreeNode.class);

	public static final TreeType TreeType_DEFAULT = TreeType.INHERITINGS;

	private final GuiTreeNode parent;
	private Generic generic;
	private List<GuiTreeNode> children = new ArrayList<>();
	private TreeType treeType = TreeType_DEFAULT;
	// private Attribute attribute;

	public enum TreeType {
		SUPERS, INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS, VALUES;
	}

	public GuiTreeNode(GuiTreeNode parent, Generic generic, TreeType treeType) {
		this.parent = parent;
		this.generic = generic;
		this.treeType = treeType;
	}

	public GuiTreeNode(GuiTreeNode parent, Generic generic) {
		this.parent = parent;
		this.generic = generic;
	}

	@SuppressWarnings("unused")
	private TreeType getTreeTypeByGeneric(Generic generic) {
		for (GuiTreeNode child : children)
			if (child.getGeneric().equals(generic))
				return child.getTreeType();
		return TreeType_DEFAULT;
	}

	public List<GuiTreeNode> getChildren() {
		return getChildren(treeType);
	}

	public List<GuiTreeNode> getChildren(TreeType treeType) {
		List<GuiTreeNode> list = new ArrayList<>();
		for (Generic child : getSnapshot(treeType))
			list.add(getChildTreeNode(child));
		children = list;
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	private <T extends Generic> Snapshot<T> getSnapshot(TreeType treeType) {
		switch (treeType) {
		case SUPERS:
			return generic.getSupers();
		case INSTANCES:
			return ((Type) generic).getInstances();
		case INHERITINGS:
			return generic.getInheritings();
		case COMPONENTS:
			return generic.getComponents();
		case COMPOSITES:
			return generic.getComposites();
		case ATTRIBUTES:
			return (Snapshot<T>) ((Type) generic).getAttributes();
			// case VALUES:
			// return (Snapshot<T>) generic.getHolders(attribute);
		default:
			break;
		}
		throw new IllegalStateException();
	}

	/**
	 * Returns a GUI tree node of one of the child of this generic.
	 * 
	 * @param child - child generric.
	 * 
	 * @return GUITreeNode object.
	 */
	private GuiTreeNode getChildTreeNode(Generic child) {
		for (GuiTreeNode childTreeNode : children)
			if (childTreeNode.getGeneric().equals(child))
				return childTreeNode;
		return new GuiTreeNode(this, child, TreeType_DEFAULT);
	}

	public String getValue() {
		return generic.toString();
	}

	public boolean isReadOnly() {
		return generic.isMeta();
	}

	public GuiTreeNode getParent() {
		return parent;
	}

	public Generic getGeneric() {
		return generic;
	}

	public void setGeneric(Generic generic) {
		this.generic = generic;
	}

	public TreeType getTreeType() {
		return treeType;
	}

	public void setTreeType(TreeType treeType) {
		this.treeType = treeType;
	}

	@Override
	public String toString() {
		return generic.toString();
	}

	// public Attribute getAttribute() {
	// return attribute;
	// }
	//
	// public void setAttribute(Attribute attribute) {
	// this.attribute = attribute;
	// }

}
