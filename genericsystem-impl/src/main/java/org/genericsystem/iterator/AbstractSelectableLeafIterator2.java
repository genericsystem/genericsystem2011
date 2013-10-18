package org.genericsystem.iterator;

import java.util.Collections;
import java.util.Iterator;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSelectableLeafIterator2 extends AbstractSelectablePostTreeIterator2<Generic> {

	public AbstractSelectableLeafIterator2(Generic root) {
		super(root);
	}

	@Override
	protected Iterator<Generic> children(final Generic father) {
		return isSelected(father) ? new ConcateIterator<Generic>(((GenericImpl) father).directInheritingsIterator(), ((GenericImpl) father).compositesIterator()) : Collections.<Generic> emptyIterator();
		// return new AbstractFilterIterator<Generic>(new ConcateIterator<Generic>(((GenericImpl) father).directInheritingsIterator(), ((GenericImpl) father).compositesIterator())) {
		// @Override
		// public boolean isSelected() {
		// return AbstractSelectableLeafIterator2.this.isSelected(father, next);
		// }
		// };
	}

	public abstract boolean isSelected(Generic father);

}
