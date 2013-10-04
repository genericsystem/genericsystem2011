package org.genericsystem.core;

import java.io.Serializable;

/**
 * @author Nicolas Feybesse
 * 
 */
public class HomeTreeNode {

	private static final String ROOT_NODE_VALUE = "Engine";

	HomeTreeNode metaNode;
	Serializable value;

	private ConcurrentWeakValueHashMap<Serializable, HomeTreeNode> instancesNodes = new ConcurrentWeakValueHashMap<>();

	private HomeTreeNode(HomeTreeNode metaNode, Serializable value) {
		this.metaNode = metaNode == null ? this : metaNode;
		this.value = value;
	}

	public HomeTreeNode findInstanceNode(Serializable value) {
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

	public HomeTreeNode getHomeTree() {
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

	@Override
	public String toString() {
		return metaNode.toString() + "|" + value;
	}

	static class RootTreeNode extends HomeTreeNode {
		RootTreeNode() {
			super(null, ROOT_NODE_VALUE);
		}

		@Override
		public boolean isRoot() {
			return true;
		}

		@Override
		public HomeTreeNode getHomeTree() {
			return this;
		}

		@Override
		public int getMetaLevel() {
			return Statics.META;
		}

		@Override
		public boolean inheritsFrom(HomeTreeNode homeTreeNode) {
			return equals(homeTreeNode);
		}

		@Override
		public String toString() {
			return "" + value;
		}
	}
}
