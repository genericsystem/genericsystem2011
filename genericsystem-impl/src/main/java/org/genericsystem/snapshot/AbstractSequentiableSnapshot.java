package org.genericsystem.snapshot;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSequentiableSnapshot<T> extends AbstractSnapshot<T> implements Collection<T> {

	private static final int INITIAL_VALUE = -1;

	private Iterator<T> previousIterator;

	private int currentIndex = INITIAL_VALUE;

	private T currentItem;

	@Override
	public T get(int index) {
		if (index != currentIndex)
			currentItem = null;
		if (index < currentIndex)
			reInit();
		return getItem(index);
	}

	private void reInit() {
		previousIterator = null;
		currentIndex = INITIAL_VALUE;
		currentItem = null;
	}

	private Iterator<T> getIterator() {
		if (previousIterator == null)
			previousIterator = iterator();
		return previousIterator;
	}

	private T getItem(int index) {
		if (currentItem == null) {
			while (currentIndex < index) {
				currentItem = getIterator().next();
				currentIndex++;
			}
		}
		return currentItem;
	}

	public abstract Iterator<T> sequentiableIterator();

	@Override
	public Iterator<T> iterator() {
		reInit();
		return sequentiableIterator();
	}

	@Override
	public Object[] toArray() {
		int i = 0;
		Object[] array = new Object[size()];
		Iterator<T> it = iterator();
		while (it.hasNext())
			array[i++] = it.next();
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U[] toArray(U[] array) {
		int i = 0;
		Iterator<T> it = iterator();
		while (it.hasNext())
			array[i++] = (U) it.next();
		return array;
	}

	@Override
	public boolean add(T e) {
		throw new IllegalStateException();
	}

	@Override
	public boolean remove(Object o) {
		throw new IllegalStateException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new IllegalStateException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new IllegalStateException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new IllegalStateException();
	}

	@Override
	public void clear() {
		throw new IllegalStateException();
	}

}
