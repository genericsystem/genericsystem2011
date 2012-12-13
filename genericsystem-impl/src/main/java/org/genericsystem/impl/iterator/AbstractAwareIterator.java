package org.genericsystem.impl.iterator;


public abstract class AbstractAwareIterator<T> extends AbstractGeneralAwareIterator<T,T> {

	@Override
	public T project() {
		return next;
	}

}
