package org.genericsystem.tree;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.NoInheritance;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Tree;
import org.genericsystem.snapshot.FunctionalSnapshot;

@InstanceGenericClass(NodeImpl.class)
@NoInheritance
public class TreeImpl extends GenericImpl implements Tree {

	@Override
	public <T extends Node> T addRoot(Serializable value) {
		return addRoot(value, 1);
	}

	@Override
	public <T extends Node> T addRoot(Serializable value, int dim) {
		return addInstance(value, new Generic[dim]);
	}

	// TODO KK
	@Override
	public <T extends Node> FunctionalSnapshot<T> getRoots() {
		return new FunctionalSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return Statics.<T> rootFilter(TreeImpl.this.<T> instancesIterator());
			}
		};
	}

	// @Override
	// public <T extends Node> Snapshot<T> getRoots() {
	// return new FunctionalSnapshot<T>() {
	// @Override
	// public Iterator<T> iterator() {
	// return Statics.<T> rootFilter(TreeImpl.this.<T> instancesIterator());
	// }
	// };
	// }

	// TODO KK
	@Override
	public <T extends Node> T getRootByValue(Serializable value) {
		return this.unambigousFirst(Statics.<T> rootFilter(Statics.<T> valueFilter(TreeImpl.this.<T> instancesIterator(), value)));
	}

	@Override
	public String getCategoryString() {
		return "Tree";
	}

	@Override
	public <T extends Node> T setRoot(Serializable value) {
		return setRoot(value, 1);
	}

	@Override
	public <T extends Node> T setRoot(Serializable value, int dim) {
		return setInstance(value, new Generic[dim]);
	}

}
