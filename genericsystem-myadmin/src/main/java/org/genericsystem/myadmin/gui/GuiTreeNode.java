package org.genericsystem.myadmin.gui;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;

/**
 * Node for the tree of generics in GUI of MyAdmin.
 * 
 * @author Alexei KLENIN - aklenin@middlewarefactory.com
 */
public class GuiTreeNode {

	public enum GuiTreeChildrenType {
		SUPERS, INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS, VALUES;
	}

	public static final GuiTreeChildrenType DEFAULT_CHILDREN_TYPE = GuiTreeChildrenType.INHERITINGS;

	private final GuiTreeNode parent;
	private Generic generic;
	private List<GuiTreeNode> children;
	private GuiTreeChildrenType childrenType = DEFAULT_CHILDREN_TYPE;

	public GuiTreeNode(GuiTreeNode parent, Generic generic, GuiTreeChildrenType treeType) {
		this.parent = parent;
		this.generic = generic;
		this.childrenType = treeType;
	}

	public GuiTreeNode(GuiTreeNode parent, Generic generic) {
		this.parent = parent;
		this.generic = generic;
	}

	public List<GuiTreeNode> getChildren() {
		return getChildren(childrenType);
	}

	public List<GuiTreeNode> getChildren(GuiTreeChildrenType childrenType) {
		if (children != null && this.childrenType == childrenType)
			return children;
		children = new ArrayList<>();
		for (Generic child : getGenericsForSubTree(childrenType))
			children.add(new GuiTreeNode(this, child));
		return children;
	}

	/**
	 * Returns the GUI tree node of generic in the sub tree of current node. Can return null if node
	 * with generic is not found.
	 * 
	 * @param generic - generic to look for.
	 * 
	 * @return the GUI tree node of generic.
	 */
	public GuiTreeNode findSubTreeNodeByGeneric(Generic generic) {
		if (this.generic == generic)
			return this;
		for (GuiTreeNode child : getChildren()) {
			GuiTreeNode found = child.findSubTreeNodeByGeneric(generic);
			if (found != null)
				return found;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> List<T> getGenericsForSubTree(GuiTreeChildrenType childrenType) {
		switch (childrenType) {
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
			return (List<T>) ((Type) generic).getAttributes();
			// case VALUES:
			// return (Snapshot<T>) generic.getHolders(attribute);
		default:
			break;
		}
		return new ArrayList<>();
		//throw new IllegalStateException();
	}

	public String getValue() {
		return generic.toString();
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

	public GuiTreeChildrenType getChildrenType() {
		return childrenType;
	}

	public void setChildrenType(GuiTreeChildrenType childrenType) {
		if (this.childrenType != childrenType)
			children = null;						// abandon precedent children
		this.childrenType = childrenType;
	}

	@Override
	public String toString() {
		return generic.toString();
	}

}
