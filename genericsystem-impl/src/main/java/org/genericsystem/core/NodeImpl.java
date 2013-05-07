package org.genericsystem.core;

import java.io.Serializable;
import java.util.Iterator;
import org.genericsystem.annotation.SystemGeneric;
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
	public <T extends Node> T setSubNode(Cache cache, Serializable value, Generic... targets) {
		Holder implicit = bindPrimary(cache, value, SystemGeneric.CONCRETE, true);
		return bind(cache, implicit, this, getBasePos(this, targets), false, targets);
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
		return thisFilter(this.<T> holdersIterator(context, attribute, getBasePos(attribute, Statics.EMPTY_GENERIC_ARRAY), false));
	}

	@Override
	public <T extends Node> T addNode(Cache cache, Serializable value, Generic... targets) {
		Holder attribute = getMeta();
		return bind(cache, ((GenericImpl) attribute).bindPrimary(cache, value, SystemGeneric.CONCRETE, true), attribute, getBasePos(attribute, targets), true, targets);
	}

	@Override
	public <T extends Node> T setNode(Cache cache, Serializable value, Generic... targets) {
		Holder attribute = getMeta();
		return bind(cache, ((GenericImpl) attribute).bindPrimary(cache, value, SystemGeneric.CONCRETE, true), attribute, getBasePos(attribute, targets), false, targets);
	}
}
