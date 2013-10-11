package org.genericsystem.myadmin.beans;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.beans.GenericBean.View;

public class TreeNode {

	private final TreeNode parent;

	private Generic generic;

	private final View currentView;

	private List<TreeNode> childrens = new ArrayList<>();

	public TreeNode(TreeNode parent, Generic generic, View currentView) {
		this.parent = parent;
		this.generic = generic;
		this.currentView = currentView;
	}

	public List<TreeNode> getChildrens(boolean implicitShow) {
		List<TreeNode> list = new ArrayList<>();
		for (Generic child : getSnapshot(generic, currentView))
			if (implicitShow || !child.isAutomatic())
				list.add(getGenericTreeNode(child));
		childrens = list;
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> Snapshot<T> getSnapshot(Generic generic, View currentView) {
		switch (currentView) {
		case SUPERS:
			return generic.getSupers();
		case INHERITINGS:
			return generic.getInheritings();
		case COMPONENTS:
			return generic.getComponents();
		case COMPOSITES:
			return generic.getComposites();
		case ATTRIBUTES:
			return (Snapshot<T>) ((Type) generic).getAttributes();
		default:
			break;
		}
		throw new IllegalStateException();
	}

	private TreeNode getGenericTreeNode(Generic child) {
		for (TreeNode old : childrens)
			if (old.getGeneric().equals(child))
				return old;
		return new TreeNode(this, child, GenericBean.DEFAULT_VIEW);
	}

	public String getValue() {
		return generic.toString();
	}

	public boolean isReadOnly() {
		return generic.isMeta();
	}

	public TreeNode getParent() {
		return parent;
	}

	public Generic getGeneric() {
		return generic;
	}

	public void setGeneric(Generic generic) {
		this.generic = generic;
	}

	public View getCurrentView() {
		return currentView;
	}

}
