package org.genericsystem.iterator;

import java.util.Iterator;

import org.genericsystem.core.Generic;

public class CartesianIterator<T> implements Iterator<T[]> {

	private final Iterable<Object>[] iterables;
	private int iterablesSize;

	private final Iterator<Object>[] iterators;

	private Object[] values;
	private boolean empty;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CartesianIterator(Iterable... iterables) {
		this.iterables = iterables;
		this.iterablesSize = iterables.length;
		this.iterators = new Iterator[iterablesSize];

		for (int i = 0; i < iterablesSize; i++) {
			iterators[i] = iterables[i].iterator();
			if (!iterators[i].hasNext()) {
				empty = true;
				break;
			}
		}

		if (!empty) {
			values = new Generic[iterablesSize];
			for (int i = 0; i < iterablesSize - 1; i++)
				setNextValue(i);
		}
	}

	@Override
	public boolean hasNext() {
		if (empty)
			return false;
		for (int i = 0; i < iterablesSize; i++)
			if (iterators[i].hasNext())
				return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] next() {
		int cursor;
		for (cursor = iterablesSize - 1; cursor >= 0; cursor--)
			if (iterators[cursor].hasNext())
				break;

		for (int i = cursor + 1; i < iterablesSize; i++)
			iterators[i] = iterables[i].iterator();

		for (int i = cursor; i < iterablesSize; i++)
			setNextValue(i);

		return (T[]) values.clone();
	}

	private void setNextValue(int index) {
		Iterator<Object> it = iterators[index];
		if (it.hasNext())
			values[index] = it.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}