package org.genericsystem.generic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;

/**
 * A Node of the Tree.
 * 
 * @author Nicolas Feybesse
 */
public interface Node extends Holder {

	/**
	 * Add a Node.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T addNode(Cache cache, Serializable value, Generic... targets);

	/**
	 * Add a Node that inherits of this.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the subNode.
	 */
	<T extends Node> T addSubNode(Cache cache, Serializable value, Generic... targets);

	/**
	 * Returns the children of this.
	 * 
	 * @param context
	 *            The reference Context.
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> Snapshot<T> getChildren(Context context);

	/**
	 * Traverse the Tree.
	 * 
	 * @param visitor
	 *            The class Visitor.
	 */
	void traverse(Visitor visitor);

	public abstract static class Visitor {

		protected final Context context;

		public Visitor(Context context) {
			this.context = context;
		}

		protected Set<Node> alreadyVisited = new HashSet<>();

		public void traverse(Node node) {
			if (alreadyVisited.add(node)) {
				before(node);
				for (Node child : node.getChildren(context))
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
