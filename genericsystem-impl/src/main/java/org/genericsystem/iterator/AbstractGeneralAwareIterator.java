package org.genericsystem.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Nicolas Feybesse
 * 
 * @param <D>
 * @param <T>
 */
public abstract class AbstractGeneralAwareIterator<D, T> implements Iterator<T> {

	public D next;
	protected boolean toRead;

	public AbstractGeneralAwareIterator() {
		this.next = null;
		this.toRead = true;
	}

	@Override
	public boolean hasNext() {
		advanceIfNeeded();
		return next != null;
	}

	@Override
	public T next() {
		advanceIfNeeded();
		if (next == null)
			throw new NoSuchElementException();
		T content = project();
		toRead = true;
		return content;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void advanceIfNeeded() {
		if (toRead) {
			advance();
			toRead = false;
		}
	}

	abstract protected void advance();

	abstract protected T project();
}
