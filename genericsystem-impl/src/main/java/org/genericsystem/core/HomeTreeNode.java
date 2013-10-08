package org.genericsystem.core;

import java.io.Serializable;
import org.genericsystem.core.EngineImpl.RootTreeNode;

/**
 * @author Nicolas Feybesse
 * 
 */
public class HomeTreeNode implements Comparable<HomeTreeNode> {

	HomeTreeNode metaNode;
	Serializable value;
	long ts;

	private ConcurrentWeakValueHashMap<Serializable, HomeTreeNode> instancesNodes = new ConcurrentWeakValueHashMap<>();

	protected HomeTreeNode(HomeTreeNode metaNode, Serializable value) {
		this.metaNode = metaNode == null ? this : metaNode;
		this.value = value;
		ts = getHomeTree().pickNewTs();
	}

	public HomeTreeNode findInstanceNode(Serializable value) {
		if (value == null)
			value = NULL_VALUE;
		return instancesNodes.get(value);
	}

	private static final String NULL_VALUE = "NULL_VALUE";

	public HomeTreeNode bindInstanceNode(Serializable value) {
		if (value == null)
			value = NULL_VALUE;
		HomeTreeNode newHomeTreeNode = new HomeTreeNode(this, value);
		HomeTreeNode result = instancesNodes.putIfAbsent(value, new HomeTreeNode(this, value));
		return result == null ? newHomeTreeNode : result;
	}

	public RootTreeNode getHomeTree() {
		return metaNode.getHomeTree();
	}

	public boolean isRoot() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <S extends Serializable> S getValue() {
		return value == NULL_VALUE ? null : (S) value;
	}

	public int getMetaLevel() {
		assert 1 + metaNode.getMetaLevel() >= 1;
		return 1 + metaNode.getMetaLevel();
	}

	public boolean inheritsFrom(HomeTreeNode homeTreeNode) {
		return equals(homeTreeNode) ? true : metaNode.inheritsFrom(homeTreeNode);
	}

	public boolean isSuperOf(HomeTreeNode homeTreeNode) {
		return equals(homeTreeNode) ? true : homeTreeNode.metaNode.isSuperOf(homeTreeNode);
	}

	@Override
	public String toString() {
		return metaNode.toString() + "|" + value;
	}

	@Override
	public int compareTo(HomeTreeNode homeTreeNode) {
		return Long.compare(ts, homeTreeNode.ts);
	}

}
