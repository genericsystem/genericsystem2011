package org.genericsystem.myadmin.beans;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;

public class GenericTreeNode {

	// TODO clean
	// private static Logger log = LoggerFactory.getLogger(GenericTreeNode.class);

	private final GenericTreeNode parent;

	private Generic generic;

	private TreeType treeType;

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

	private TreeType getTreeType(Generic generic) {
		for (GenericTreeNode child : childrens)
			if (child.getGeneric().equals(generic))
				return child.getTreeType();
		return TreeType_DEFAULT;
	}

	@SuppressWarnings("unchecked")
	private <T extends Generic> Snapshot<T> getSnapshot(Cache cache) {
		switch (treeType) {
		case SUPERS:
			return generic.getSupers();
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
		case VALUES:
			return (Snapshot<T>) generic.getHolders(cache, cache.getMetaAttribute());
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

	public List<GenericTreeNode> getChildrens(Cache cache, boolean implicitShow) {
		List<GenericTreeNode> list = new ArrayList<>();
		for (Generic child : getSnapshot(cache))
			if (implicitShow || !isImplicitAutomatic(child))
				list.add(getGenericTreeNode(child));
		childrens = list;
		return list;
	}

	public boolean isImplicitAutomatic(Generic generic) {
		return generic.isAutomatic() && ((GenericImpl) generic).isPrimary();
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
