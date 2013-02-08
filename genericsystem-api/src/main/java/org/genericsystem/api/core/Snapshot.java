package org.genericsystem.api.core;

import java.util.Collection;

/**
 * @author Nicolas Feybesse
 * 
 * @param <T>
 */
public interface Snapshot<T> extends Iterable<T> {

	int size();

	T get(int index);

	boolean isEmpty();

	boolean contains(Object object);

	boolean containsAll(Collection<?> c);

	boolean containsAll(Snapshot<?> c);

	Snapshot<T> filter(Filter<T> filter);

	<E> Snapshot<E> project(Projector<E, T> filter);

	static interface Filter<T> {
		boolean isSelected(T element);
	}

	static interface Projector<T, E> {
		T project(E element);
	}

	void log();

	T first();
}
