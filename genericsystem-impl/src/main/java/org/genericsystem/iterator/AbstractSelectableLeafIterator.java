package org.genericsystem.iterator;

import java.util.Iterator;

import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;

public abstract class AbstractSelectableLeafIterator extends AbstractSelectablePostTreeIterator<Generic> {

	private final Context context;

	public AbstractSelectableLeafIterator(Context context, Generic root) {
		super(root);
		this.context = context;
	}

	@Override
	protected Iterator<Generic> children(final Generic father) {
		return new AbstractFilterIterator<Generic>(((GenericImpl) father).directInheritingsIterator(context)) {
			@Override
			public boolean isSelected() {
				return AbstractSelectableLeafIterator.this.isSelected(father, next);
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
