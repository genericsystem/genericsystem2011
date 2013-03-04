package org.genericsystem.iterator;

import java.util.Iterator;

public abstract class AbstractFilterIterator<T> extends AbstractProjectorAndFilterIterator<T, T> {

	public AbstractFilterIterator(Iterator<T> iterator) {
		super(iterator);
	}

	@Override
	protected T project() {
		return next;
	}

}
