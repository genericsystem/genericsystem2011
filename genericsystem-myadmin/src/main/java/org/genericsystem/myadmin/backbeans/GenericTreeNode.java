package org.genericsystem.myadmin.backbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;

public class GenericTreeNode {

	// TODO clean
	// private static Logger log = LoggerFactory.getLogger(GenericTreeNode.class);

	private GenericTreeNode parent;

	private Generic generic;

	private Map<Generic, TreeType> mapTreeTypes = new HashMap<>();

	public GenericTreeNode(Generic generic) {
		this.parent = this;
		this.generic = generic;
		mapTreeTypes.put(generic, TreeType.INSTANCES);
	}

	public GenericTreeNode(GenericTreeNode parent, Generic generic) {
		this.parent = parent;
		this.generic = generic;
		mapTreeTypes.put(generic, TreeType.INSTANCES);
	}

	public GenericTreeNode getParent() {
		return parent;
	}

	public Generic getGeneric() {
		return generic;
	}

	public enum TreeType {
		INSTANCES, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES, RELATIONS;
	}

	public List<GenericTreeNode> getChildrens(Cache cache) {
		switch (mapTreeTypes.get(generic)) {
		case INSTANCES: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : ((Type) generic).getInstances(cache))
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		case INHERITINGS: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : generic.getInheritings(cache))
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		case COMPONENTS: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : generic.getComponents())
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		case COMPOSITES: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : generic.getComposites(cache))
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		case ATTRIBUTES: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : ((Type) generic).getAttributes(cache))
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		case RELATIONS: {
			List<GenericTreeNode> list = new ArrayList<>();
			for (Generic child : ((Type) generic).getRelations(cache))
				list.add(newGenericTreeNode(this, generic, child));
			return list;
		}
		default:
			break;
		}
		throw new IllegalStateException();
	}

	private GenericTreeNode newGenericTreeNode(GenericTreeNode genericTreeNode, Generic parent, Generic child) {
		GenericTreeNode treeNode = new GenericTreeNode(this, child);
		treeNode.setTreeType(child, getTreeType(mapTreeTypes.get(child) != null ? child : parent));
		return treeNode;
	}

	public String getValue() {
		return generic.toString();
	}

	public boolean isReadOnly() {
		return generic.isMeta();
	}

	public TreeType getTreeType(Generic selectedType) {
		return mapTreeTypes.get(selectedType);
	}

	public void setTreeType(Generic selectedType, TreeType treeType) {
		if (null != treeType)
			mapTreeTypes.put(selectedType, treeType);
	}

}
