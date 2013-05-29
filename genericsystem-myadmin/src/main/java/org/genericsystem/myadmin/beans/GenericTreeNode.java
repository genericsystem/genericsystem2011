package org.genericsystem.myadmin.beans;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;

public class GenericTreeNode {

	// TODO clean
	// private static Logger log = LoggerFactory.getLogger(GenericTreeNode.class);

	private final GenericTreeNode parent;

	private Generic generic;

	private TreeType treeType;

	private List<GenericTreeNode> childrens = new ArrayList<>();

	public static final TreeType TreeType_DEFAULT = TreeType.INSTANCES;

	public enum TreeType {
		INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS;
	}

	public GenericTreeNode(GenericTreeNode parent, Generic generic, TreeType treeType) {
		this.parent = parent;
		this.generic = generic;
		this.treeType = treeType;
	}

	private TreeType getTreeType(Generic generic) {
		for (GenericTreeNode child : childrens)
			if (child.getGeneric().equals(generic))
				return child.getTreeType();
		return TreeType_DEFAULT;
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> Snapshot<T> getSnapshot(Cache cache) {
		switch (treeType) {
		case INSTANCES:
			return ((Type) generic).getInstances(cache);
		case INHERITINGS:
			return generic.getInheritings(cache);
		case COMPONENTS:
			return generic.getComponents();
		case COMPOSITES:
			return generic.getComposites(cache);
		case ATTRIBUTES:
			return (Snapshot<T>) ((Type) generic).getAttributes(cache);
		case RELATIONS:
			return (Snapshot<T>) ((Type) generic).getRelations(cache);
		}
		throw new IllegalStateException();
	}

	private GenericTreeNode getGenericTreeNode(Generic child) {
		for (GenericTreeNode old : childrens)
			if (old.getGeneric().equals(child))
				return old;
		return new GenericTreeNode(this, child, getTreeType(child));
	}

	public List<GenericTreeNode> getChildrens(Cache cache) {
		List<GenericTreeNode> list = new ArrayList<>();
		for (Generic child : getSnapshot(cache))
			list.add(getGenericTreeNode(child));
		childrens = list;
		return list;
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

}
