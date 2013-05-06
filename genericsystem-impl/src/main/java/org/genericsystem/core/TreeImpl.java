package org.genericsystem.core;

import java.io.Serializable;
import java.util.Iterator;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Tree;
import org.genericsystem.snapshot.AbstractSnapshot;

public class TreeImpl extends GenericImpl implements Tree {

	@Override
	public <T extends Node> T newRoot(Cache cache, Serializable value) {
		return newRoot(cache, value, 1);
	}

	@Override
	public <T extends Node> T newRoot(Cache cache, Serializable value, int dim) {
		return newInstance(cache, value, new Generic[dim]);
	}

	// TODO KK
	@Override
	public <T extends Node> Snapshot<T> getRoots(final Context context) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return Statics.<T> rootFilter(TreeImpl.this.<T> instancesIterator(context));
			}
		};
	}

	// TODO KK
	@Override
	public <T extends Node> T getRootByValue(Context context, Serializable value) {
		return Statics.unambigousFirst(Statics.<T> rootFilter(Statics.<T> valueFilter(TreeImpl.this.<T> instancesIterator(context), value)));
	}
}
