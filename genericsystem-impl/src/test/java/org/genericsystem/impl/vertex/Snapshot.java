package org.genericsystem.impl.vertex;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractProjectionIterator;

public abstract class Snapshot<T> implements Iterable<T> {

	int size() {
		Iterator<T> iterator = iterator();
		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	@FunctionalInterface
	public interface Filter<T> {
		boolean isSelected(T candidate);
	}

	Snapshot<T> filter(final Filter<T> filter) {
		return new Snapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return new AbstractFilterIterator<T>(Snapshot.this.iterator()) {
					@Override
					public boolean isSelected() {
						return filter.isSelected(next);
					}
				};
			}
		};
	}

	@FunctionalInterface
	static interface Projector<T, E> {
		T project(E element);
	}

	public <E> Snapshot<E> project(final Projector<E, T> projector) {
		return new Snapshot<E>() {
			@Override
			public Iterator<E> iterator() {
				return new AbstractProjectionIterator<T, E>(Snapshot.this.iterator()) {
					@Override
					public E project(T t) {
						return projector.project(t);
					}
				};
			}
		};
	}

	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	public boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	public String toString() {
		Iterator<T> it = iterator();
		if (!it.hasNext())
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			T e = it.next();
			sb.append(e == this ? "(this Collection)" : e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Snapshot))
			return false;
		Snapshot s = (Snapshot) o;
		Iterator<T> e1 = iterator();
		Iterator e2 = s.iterator();
		while (e1.hasNext() && e2.hasNext()) {
			T o1 = e1.next();
			Object o2 = e2.next();
			if (!(Objects.equals(o1, o2)))
				return false;
		}
		return !(e1.hasNext() || e2.hasNext());
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (T e : this)
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		return hashCode;
	}

	public T get(T o) {
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (o.equals(next))
				return next;
		}
		return null;
	}

	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
}
