package org.genericsystem.impl.iterator;

import java.util.Iterator;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.impl.core.GenericImpl;

public abstract class AbstractSelectableLeafInheritedIterator extends AbstractSelectablePostTreeIterator<Generic> {

	private final Context context;

	public AbstractSelectableLeafInheritedIterator(Context context, Generic root) {
		super(root);
		this.context = context;
	}

	@Override
	protected Iterator<Generic> children(final Generic father) {
		return new AbstractFilterIterator<Generic>(((GenericImpl) father).directInheritingsIterator(context)) {
			@Override
			public boolean isSelected() {
				return AbstractSelectableLeafInheritedIterator.this.isSelected(father, next);
			}
		};

		// return new ConcateIterator<Generic>(new AbstractFilterIterator<Generic>(((GenericImpl) father).directInheritingsIterator(context)) {
		// @Override
		// public boolean isSelected() {
		// return AbstractSelectableLeafInheritedIterator.this.isSelected(father, next);
		// }
		// }, new AbstractConcateIterator<Integer, Generic>(((GenericImpl) father).treeComponentsPosIterator()) {
		//
		// @Override
		// protected Iterator<Generic> getIterator(final Integer componentPos) {
		// return new AbstractFilterIterator<Generic>(((GenericImpl) father).compositesIterator(context, componentPos)) {
		//
		// @Override
		// public boolean isSelected() {
		// return !next.equals(father) && (father.getComponentsSize() == next.getComponentsSize()) && AbstractSelectableLeafInheritedIterator.this.isSelected(father, next);
		// }
		// };
		// }
		// });
	}

	protected abstract boolean isSelected(Generic father, Generic candidate);

}
