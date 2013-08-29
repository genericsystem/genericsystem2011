package org.genericsystem.tree;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.annotation.SystemGeneric;
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
	public <T extends Node> T getChild(Serializable value) {
		Tree attribute = getMeta();
		return Statics.unambigousFirst(Statics.<T> valueFilter(this.<T> thisFilter(this.<T> holdersIterator(attribute, getBasePos(attribute), value == null)), value));
	}

	@Override
	public void traverse(Visitor visitor) {
		visitor.traverse(this);
	}

	@Override
	public <T extends Node> Snapshot<T> getChildren() {
		return getChildren(getBasePos(this.<Tree> getMeta()));
	}

	@Override
	public <T extends Node> Snapshot<T> getChildren(final int basePos) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return childrenIterator(basePos);
			}
		};
	}

	<T extends Generic> Iterator<T> childrenIterator(int basePos) {
		return thisFilter(this.<T> holdersIterator(SystemGeneric.CONCRETE, this.<Tree> getMeta(), basePos, false));
	}

	@Override
	public <T extends Node> T addNode(Serializable value, Generic... targets) {
		return addHolder(this.<Holder> getMeta(), value, targets);
	}

	@Override
	public <T extends Node> T setNode(Serializable value, Generic... targets) {
		return setHolder(this.<Holder> getMeta(), value, targets);
	}

	@Override
	public <T extends Node> T setSubNode(Serializable value, Generic... targets) {
		return setHolder(this, value, targets);
	}

	@Override
	public String getCategoryString() {
		return "Node";
	}
}
