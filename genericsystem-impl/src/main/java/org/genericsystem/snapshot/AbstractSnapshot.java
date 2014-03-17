package org.genericsystem.snapshot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.genericsystem.core.Snapshot;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 *
 */
@FunctionalInterface
public interface AbstractSnapshot<T> extends Snapshot<T> {

	static Logger log = LoggerFactory.getLogger(AbstractSnapshot.class);

	// methods of Snapshot

	@Override
	public default AbstractSnapshot<T> filter(final Filter<T> filter) {
		return () -> new AbstractFilterIterator<T>(AbstractSnapshot.this.iterator()) {
			@Override
			public boolean isSelected() {
				return filter.isSelected(next);
			}
		};
	}

	@Override
	public default <E> AbstractSnapshot<E> project(final Projector<E, T> projector) {
		return () -> new AbstractProjectionIterator<T, E>(AbstractSnapshot.this.iterator()) {
			@Override
			public E project(T t) {
				return projector.project(t);
			}
		};
	}

	@Override
	public default Object[] toArray() {
		// Estimate size of array; be prepared to see more or fewer elements
		Object[] r = new Object[size()];
		Iterator<T> it = iterator();
		for (int i = 0; i < r.length; i++) {
			if (!it.hasNext()) // fewer elements than expected
				return Arrays.copyOf(r, i);
			r[i] = it.next();
		}
		return it.hasNext() ? finishToArray(r, it) : r;

	}

	@SuppressWarnings("unchecked")
	static <T> T[] finishToArray(T[] r, Iterator<?> it) {
		int i = r.length;
		while (it.hasNext()) {
			int cap = r.length;
			if (i == cap) {
				int newCap = cap + (cap >> 1) + 1;
				// overflow-conscious code
				if (newCap - MAX_ARRAY_SIZE > 0)
					newCap = hugeCapacity(cap + 1);
				r = Arrays.copyOf(r, newCap);
			}
			r[i++] = (T) it.next();
		}
		// trim if overallocated
		return (i == r.length) ? r : Arrays.copyOf(r, i);
	}

	int MAX_ARRAY_SIZE = 2147483639;

	static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError("Required array size too large");
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public default <E> E[] toArray(E[] a) {
		// Estimate size of array; be prepared to see more or fewer elements
		int size = size();
		E[] r = a.length >= size ? a : (E[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		Iterator<T> it = iterator();

		for (int i = 0; i < r.length; i++) {
			if (!it.hasNext()) { // fewer elements than expected
				if (a == r) {
					r[i] = null; // null-terminate
				} else if (a.length < i) {
					return Arrays.copyOf(r, i);
				} else {
					System.arraycopy(r, 0, a, 0, i);
					if (a.length > i) {
						a[i] = null;
					}
				}
				return a;
			}
			r[i] = (E) it.next();
		}
		// more elements than expected
		return it.hasNext() ? finishToArray(r, it) : r;
	}

	@Override
	public default boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	public default void log() {
		log.info(toString());
	}

	// methods of List

	@Override
	public default T get(int pos) {
		int i = 0;
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (pos == i++)
				return next;
		}
		return null;
	}

	@Override
	public default boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default int indexOf(Object o) {
		int i = 0;
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			if (o.equals(it.next()))
				return i;
			i++;
		}
		return -1;
	}

	@Override
	public default int lastIndexOf(Object o) {
		return indexOf(o);
	}

	@Override
	public default boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public default ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	// methods of Collection

	@Override
	public default int size() {
		int size = 0;
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	@Override
	public default boolean isEmpty() {
		return !iterator().hasNext();
	}

	@Override
	public default boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	@Override
	public default boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default void clear() {
		throw new UnsupportedOperationException();
	}

	// @SuppressWarnings("rawtypes")
	// @Override
	// public default boolean equals(Object o) {
	// if (o == this)
	// return true;
	// if (!(o instanceof Snapshot))
	// return false;
	// Snapshot s = (Snapshot) o;
	//
	// Iterator<T> e1 = iterator();
	// Iterator e2 = s.iterator();
	// while (e1.hasNext() && e2.hasNext()) {
	// T o1 = e1.next();
	// Object o2 = e2.next();
	// if (!(o1 == null ? o2 == null : o1.equals(o2)))
	// return false;
	// }
	// return !(e1.hasNext() || e2.hasNext());
	// }
	//
	// @Override
	// public default int hashCode() {
	// int hashCode = 1;
	// for (T e : this)
	// hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
	// return hashCode;
	// }
}