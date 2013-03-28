package org.genericsystem.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Nicolas Feybesse
 * 
 * @param <T>
 */
public class SingletonIterator<T> implements Iterator<T> {

	private T singleton;

	public SingletonIterator(T singleton) {
		assert singleton != null;
		this.singleton = singleton;
	}

	private boolean hasNext = true;

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if (hasNext) {
			hasNext = false;
			return singleton;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
