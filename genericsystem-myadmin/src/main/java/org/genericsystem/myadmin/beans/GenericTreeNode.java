package org.genericsystem.myadmin.beans;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;

public class GenericTreeNode {
	// protected static Logger log = LoggerFactory.getLogger(GenericTreeNode.class);

	private final GenericTreeNode parent;

	private Generic generic;

	private TreeType treeType;

	// private Attribute attribute;

	private List<GenericTreeNode> childrens = new ArrayList<>();

	public static final TreeType TreeType_DEFAULT = TreeType.INHERITINGS;

	public enum TreeType {
		SUPERS, INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS, VALUES;
	}

	public GenericTreeNode(GenericTreeNode parent, Generic generic, TreeType treeType) {
		this.parent = parent;
		this.generic = generic;
		this.treeType = treeType;
	}

	@SuppressWarnings("hiding")
	private TreeType getTreeType(Generic generic) {
		for (GenericTreeNode child : childrens)
			if (child.getGeneric().equals(generic))
				return child.getTreeType();
		return TreeType_DEFAULT;
	}

	public List<GenericTreeNode> getChildrens(boolean implicitShow) {
		return getChildrens(treeType, implicitShow);
	}

	@SuppressWarnings("hiding")
	public List<GenericTreeNode> getChildrens(TreeType treeType, boolean implicitShow) {
		List<GenericTreeNode> list = new ArrayList<>();
		for (Generic child : getSnapshot(treeType)) {
			// if (implicitShow || !isImplicitAutomatic(child))
			list.add(getGenericTreeNode(child));
		}
		childrens = list;
		return list;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
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

	private GenericTreeNode getGenericTreeNode(Generic child) {
		for (GenericTreeNode old : childrens)
			if (old.getGeneric().equals(child))
				return old;
		return new GenericTreeNode(this, child, getTreeType(child));
	}

	@SuppressWarnings("hiding")
	public boolean isImplicitAutomatic(Generic generic) {
		return ((GenericImpl) generic).getComponentsSize() == 0;
	}

	public String getValue() {
		return generic.toString();
	}

	public boolean isReadOnly() {
		return generic.isMeta();
	}

	public GenericTreeNode getParent() {
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
