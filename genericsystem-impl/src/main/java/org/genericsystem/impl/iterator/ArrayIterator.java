package org.genericsystem.impl.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {

	private final T[] array;
	private int index = 0;

	@SafeVarargs
	public ArrayIterator(T... array) {
		this.array = array;
	}

	@Override
	public boolean hasNext() {
		return (index < array.length);
	}

	@Override
	public T next() throws NoSuchElementException {
		if (index >= array.length)
			throw new NoSuchElementException("Array index: " + index);
		T object = array[index];
		index++;
		return object;
	}

	@Override
	public void remove() {
		throw new IllegalStateException();
	}

	public int getIndex() {
		return index - 1;
	}
};