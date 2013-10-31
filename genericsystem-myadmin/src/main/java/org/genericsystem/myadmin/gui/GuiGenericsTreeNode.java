package org.genericsystem.myadmin.gui;

import java.util.ArrayList;
import java.util.Iterator;
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
		for (Generic child : getChildrenGenerics(childrenType))
			children.add(new GuiGenericsTreeNode(this, child));
		return children;
	}

	/**
	 * Returns the GUI tree node of generic from children of current node. Can return null if node
	 * with generic was not found.
	 * 
	 * @param generic - generic to look for.
	 * 
	 * @return the GUI tree node of generic.
	 */
	public GuiGenericsTreeNode findChildNodeByGeneric(Generic generic) {
		if (children != null)
			for (GuiGenericsTreeNode childNode : children)
				if (childNode.generic == generic)
					return childNode;
		return null;
	}

	/**
	 * Returns the GUI tree node of generic in the sub tree of current node. Can return null if node
	 * with generic was not found.
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
	@Deprecated
	public void abandonChildren() {
		children = null;
	}

	/**
	 * Update current list of children. If node of child is already present in the list of children
	 * it's state is not changes. If the new child of generic found it's node will be added in the
	 * list. If child not more exists it's node will be removed from the list.
	 */
	public void updateChildren() {
		if (children != null) {
			for (Generic child : getChildrenGenerics()) {
				if (findChildNodeByGeneric(child) == null)
					children.add(new GuiGenericsTreeNode(this, child));
			}
			Iterator<GuiGenericsTreeNode> iterator = children.iterator();
			while (iterator.hasNext()) {
				GuiGenericsTreeNode childNode = iterator.next();
				if (!getChildrenGenerics().contains(childNode.generic))
					iterator.remove();
			}
		}
	}

	/**
	 * Update it's own children and children of it's children. Recursive method.
	 */
	public void updateSubTree() {
		updateChildren();
		if (children != null)
			for (GuiGenericsTreeNode child : children)
				child.updateSubTree();
	}

	private <T extends Generic> List<T> getChildrenGenerics() {
		return getChildrenGenerics(childrenType);
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> List<T> getChildrenGenerics(GuiTreeChildrenType childrenType) {
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

	public void collapse() {
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
