package org.genericsystem.generic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;

/**
 * A Node of the Tree.
 * 
 * @author Nicolas Feybesse
 */
public interface Node extends Holder {

	/**
	 * Add a new node or throws an exception if this node already exists
	 * 
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T addNode(Serializable value, Generic... targets);

	/**
	 * Add a new node or returns this node if already exists.
	 * 
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T setNode(Serializable value, Generic... targets);

	/**
	 * Add an inheriting subNode if not exists, return existent inheriting subNode otherwise.
	 * 
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the subNode.
	 */
	<T extends Node> T setSubNode(Serializable value, Generic... targets);

	/**
	 * Returns the children of this.
	 * 
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> Snapshot<T> getChildren();

	/**
	 * Returns the children of this.
	 * 
	 * @param basePos
	 *            The base position.
	 * 
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> Snapshot<T> getChildren(int basePos);

	/**
	 * Returns the child of this.
	 * 
	 * @Param value The value
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> T getChild(Serializable value);

	/**
	 * Traverse the Tree.
	 * 
	 * @param visitor
	 *            The class Visitor.
	 */
	void traverse(Visitor visitor);

	public abstract static class Visitor {

		protected Set<Node> alreadyVisited = new HashSet<>();

		public void traverse(Node node) {
			if (alreadyVisited.add(node)) {
				before(node);
				for (Node child : node.getChildren())
					traverse(child);
				after(node);
			}
		}

		public void before(Node node) {

		}

		public void after(Node node) {

		}
	}

}
