package org.genericsystem.api.generic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface Node extends Holder {

	<T extends Node> T addNode(Cache cache, Serializable value, Generic... targets);

	<T extends Node> T addSubNode(Cache cache, Serializable value, Generic... targets);

	<T extends Node> Snapshot<T> getChildren(Context context);

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
