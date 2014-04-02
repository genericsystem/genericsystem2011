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

	// @Override

	@Override
	default public <E> AbstractSnapshot<E> project(final Projector<E, T> projector) {
		return () -> new AbstractProjectionIterator<T, E>(AbstractSnapshot.this.iterator()) {
			@Override
			public E project(T t) {
				return projector.project(t);
			}
		};
	}

	@Override
	default public Snapshot<T> filter(final Filter<T> filter) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return new AbstractFilterIterator<T>(AbstractSnapshot.this.iterator()) {
					@Override
					public boolean isSelected() {
						return filter.isSelected(next);
					}
				};
			}
		};
	}

	// @Override
	@Override
	default public void log() {
		log.info(toString());
	}

	// methods of List

	// @Override
	@Override
	default public T get(int pos) {
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
	default public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public int indexOf(Object o) {
		int i = 0;
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			if (o.equals(it.next()))
				return i;
			i++;
		}
		return -1;
	}

	// @Override
	@Override
	default public int lastIndexOf(Object o) {
		return indexOf(o);
	}

	// @Override
	@Override
	default public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	// methods of Collection

	// @Override
	@Override
	default public int size() {
		int size = 0;
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	// @Override
	@Override
	default public boolean isEmpty() {
		return !iterator().hasNext();
	}

	// @Override
	@Override
	default public boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	@Override
	default public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	// @Override
	@Override
	default public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	// @Override
	@Override
	default public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	default public Object[] toArray() {
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

	static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError("Required array size too large");
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}

	@SuppressWarnings("hiding")
	@Override
	default public <F> F[] toArray(F[] a) {
		// Estimate size of array; be prepared to see more or fewer elements
		int size = size();
		F[] r = a.length >= size ? a : (F[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
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
			r[i] = (F) it.next();
		}
		// more elements than expected
		return it.hasNext() ? finishToArray(r, it) : r;
	}

	// @Override
	// @SuppressWarnings("rawtypes")
	// // @Override
	// public boolean equals(Object o) {
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

	// // @Override
	// @Override
	// public int hashCode() {
	// int hashCode = 1;
	// for (T e : this)
	// hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
	// return hashCode;
	// }
}