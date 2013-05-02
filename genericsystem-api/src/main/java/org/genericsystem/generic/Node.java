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
	 * Add a Node if not exists, return existent node otherwise.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T setNode(Cache cache, Serializable value, Generic... targets);

	/**
	 * Add an inheriting subNode if not exists, return existent inheriting subNode otherwise.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the subNode.
	 */
	<T extends Node> T setSubNode(Cache cache, Serializable value, Generic... targets);

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
	 * Returns the child of this.
	 * 
	 * @param context
	 *            The reference Context.
	 * @Param value The value
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> T getChild(Context context, Serializable value);

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
