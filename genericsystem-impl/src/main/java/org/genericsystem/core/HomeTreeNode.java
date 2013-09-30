package org.genericsystem.core;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nicolas Feybesse
 * 
 */
public class HomeTreeNode {

	private static final String ROOT_NODE_VALUE = "Engine";

	private HomeTreeNode metaNode;
	private Serializable value;

	// TODO WEAK or SOFT references ?
	private ConcurrentHashMap<Serializable, HomeTreeNode> instancesNodes = new ConcurrentHashMap<>();

	private HomeTreeNode(HomeTreeNode metaNode, Serializable value) {
		this.metaNode = metaNode;
		this.value = value;
	}

	public HomeTreeNode findInstanceNode(Serializable value) {
		return instancesNodes.get(value);
	}

	public HomeTreeNode bindInstanceNode(Serializable value) {
		return instancesNodes.putIfAbsent(value, new HomeTreeNode(this, value));
	}

	public HomeTreeNode getHomeTree() {
		return metaNode.getHomeTree();
	}

	public boolean isRoot() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <S extends Serializable> S getValue() {
		return (S) value;
	}

	public int getMetaLevel() {
		return metaNode.getMetaLevel() + 1;
	}

	public boolean inheritsFrom(HomeTreeNode homeTreeNode) {
		return equals(homeTreeNode) ? true : metaNode.inheritsFrom(homeTreeNode);
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
	}
}
