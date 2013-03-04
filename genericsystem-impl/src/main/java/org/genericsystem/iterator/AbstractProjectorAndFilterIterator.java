package org.genericsystem.iterator;

import java.util.Iterator;

public abstract class AbstractProjectorAndFilterIterator<D, T> extends AbstractGeneralAwareIterator<D, T> {

	private Iterator<D> iterator;

	public AbstractProjectorAndFilterIterator(Iterator<D> iterator) {
		this.iterator = iterator;
	}

	@Override
	protected void advance() {
		while (iterator.hasNext()) {
			next = iterator.next();
			if (isSelected())
				return;
		}
		next = null;
	}

	public abstract boolean isSelected();
}
