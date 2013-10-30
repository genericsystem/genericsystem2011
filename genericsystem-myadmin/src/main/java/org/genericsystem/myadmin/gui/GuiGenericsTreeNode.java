package org.genericsystem.myadmin.gui;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node for the tree of generics in GUI of MyAdmin.
 * 
 * @author Alexei KLENIN - aklenin@middlewarefactory.com
 */
public class GuiGenericsTreeNode {

	protected static Logger log = LoggerFactory.getLogger(GenericImpl.class);

	public enum GuiTreeChildrenType {
		SUPERS, INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS, VALUES;
	}

	public static final GuiTreeChildrenType DEFAULT_CHILDREN_TYPE = GuiTreeChildrenType.INHERITINGS;

	private final GuiGenericsTreeNode parent;
	private Generic generic;
	private List<GuiGenericsTreeNode> children;
	private GuiTreeChildrenType childrenType = DEFAULT_CHILDREN_TYPE;
	private boolean expanded = false;

	public GuiGenericsTreeNode(GuiGenericsTreeNode parent, Generic generic, GuiTreeChildrenType treeType) {
		this.parent = parent;
		this.generic = generic;
		this.childrenType = treeType;
	}

	public GuiGenericsTreeNode(GuiGenericsTreeNode parent, Generic generic) {
		this.parent = parent;
		this.generic = generic;
	}

	public List<GuiGenericsTreeNode> getChildren() {
		return getChildren(childrenType);
	}

	public List<GuiGenericsTreeNode> getChildren(GuiTreeChildrenType childrenType) {
		if (children != null && this.childrenType == childrenType)
			return children;
		children = new ArrayList<>();
		for (Generic child : getGenericsForSubTree(childrenType))
			children.add(new GuiGenericsTreeNode(this, child));
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
	public GuiGenericsTreeNode findSubTreeNodeByGeneric(Generic generic) {
		if (this.generic == generic)
			return this;
		for (GuiGenericsTreeNode child : getChildren()) {
			GuiGenericsTreeNode found = child.findSubTreeNodeByGeneric(generic);
			if (found != null)
				return found;
		}
		return null;
	}

	/**
	 * Abandon current children to force the rebuild of the subtree.
	 */
	public void abandonChildren() {
		children = null;
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
		case VALUES:
			return (List<T>) generic.getComponents().get(0).getHolders((Attribute) generic);
		default:
			break;
		}
		return new ArrayList<>();
		//throw new IllegalStateException();
	}

	public void expand() {
		expanded = true;
		if (parent != null)
			parent.expand();
	}

	public void reduce() {
		expanded = false;
	}

	public String getValue() {
		return generic.toString();
	}

	public GuiGenericsTreeNode getParent() {
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

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
