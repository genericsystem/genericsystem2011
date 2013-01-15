package org.genericsystem.impl.iterator;

import java.util.Iterator;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.impl.core.GenericImpl;

public abstract class AbstractLeafInheritedIterator extends AbstractSelectablePostTreeIterator<Generic> {

	private final Context context;

	public AbstractLeafInheritedIterator(Context context, Generic root) {
		super(root);
		this.context = context;
	}

	@Override
	protected Iterator<Generic> children(final Generic father) {
		return new AbstractFilterIterator<Generic>(((GenericImpl) father).directInheritingsIterator(context)) {
			@Override
			public boolean isSelected() {
				return AbstractLeafInheritedIterator.this.isSelected(next);
			}
		};
	}

	protected abstract boolean isSelected(Generic candidate);

}
