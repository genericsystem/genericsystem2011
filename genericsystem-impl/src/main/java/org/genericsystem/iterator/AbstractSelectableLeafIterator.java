package org.genericsystem.iterator;

import java.util.Iterator;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSelectableLeafIterator extends AbstractSelectablePostTreeIterator<Generic> {

	public AbstractSelectableLeafIterator(Generic root) {
		super(root);
	}

	@Override
	protected Iterator<Generic> children(final Generic father) {
		return new AbstractFilterIterator<Generic>(((GenericImpl) father).inheritingsAndInstancesIterator()) {
			@Override
			public boolean isSelected() {
				return AbstractSelectableLeafIterator.this.isSelected(next);
			}
		};
	}

	public abstract boolean isSelected(Generic candidate);

}
