package org.genericsystem.core;

import java.io.Serializable;
import org.genericsystem.core.ConcurrentRefValueHashMap.ConcurrentWeakValueHashMap;
import org.genericsystem.core.EngineImpl.RootTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class HomeTreeNode implements Comparable<HomeTreeNode> {

	protected static Logger log = LoggerFactory.getLogger(HomeTreeNode.class);

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
		HomeTreeNode result = findInstanceNode(value);
		if (result != null)
			return result;
		HomeTreeNode newHomeTreeNode = new HomeTreeNode(this, value);
		result = instancesNodes.putIfAbsent(value, newHomeTreeNode);
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
		return this.equals(homeTreeNode) ? true : metaNode.inheritsFrom(homeTreeNode);
	}

	@Override
	public String toString() {
		return metaNode.toString() + "|" + getValue();
	}

	@Override
	public int compareTo(HomeTreeNode homeTreeNode) {
		return Long.compare(ts, homeTreeNode.ts);
	}

}
