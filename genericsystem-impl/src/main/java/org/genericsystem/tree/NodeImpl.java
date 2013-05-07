package org.genericsystem.tree;

import java.io.Serializable;
import java.util.Iterator;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Tree;
import org.genericsystem.snapshot.AbstractSnapshot;

public class NodeImpl extends GenericImpl implements Node {
	@Override
	public <T extends Node> T getChild(Context context, Serializable value) {
		Tree attribute = getMeta();
		return Statics.unambigousFirst(Statics.<T> valueFilter(this.<T> thisFilter(this.<T> holdersIterator(context, attribute, getBasePos(attribute, Statics.EMPTY_GENERIC_ARRAY), value == null)), value));
	}

	@Override
	public void traverse(Visitor visitor) {
		visitor.traverse(this);
	}

	@Override
	public <T extends Node> Snapshot<T> getChildren(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return childrenIterator(context);
			}
		};
	}

	<T extends Generic> Iterator<T> childrenIterator(Context context) {
		Tree attribute = getMeta();
		return thisFilter(this.<T> holdersIterator(context, attribute, getBasePos(attribute), false));
	}

	@Override
	public <T extends Node> T addNode(Cache cache, Serializable value, Generic... targets) {
		return addHolder(cache, this.<Holder> getMeta(), value, targets);
	}

	@Override
	public <T extends Node> T setNode(Cache cache, Serializable value, Generic... targets) {
		return setHolder(cache, this.<Holder> getMeta(), value, targets);
	}

	@Override
	public <T extends Node> T setSubNode(Cache cache, Serializable value, Generic... targets) {
		return setHolder(cache, this, value, targets);
	}
}
