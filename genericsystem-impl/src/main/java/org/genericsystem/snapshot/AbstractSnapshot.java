package org.genericsystem.snapshot;

import java.util.AbstractSet;
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
public abstract class AbstractSnapshot<T> extends AbstractSet<T> implements Snapshot<T> {

	protected static Logger log = LoggerFactory.getLogger(AbstractSnapshot.class);

	// methods of Snapshot

	@Override
	public Snapshot<T> filter(final Filter<T> filter) {
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

	@Override
	public <E> Snapshot<E> project(final Projector<E, T> projector) {
		return new AbstractSnapshot<E>() {
			@Override
			public Iterator<E> iterator() {
				return new AbstractProjectionIterator<T, E>(AbstractSnapshot.this.iterator()) {
					@Override
					public E project(T t) {
						return projector.project(t);
					}
				};
			}

		};
	}

	@Override
	public void log() {
		log.info(toString());
	}

	// methods of List

	@Override
	public T get(int pos) {
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
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
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
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	// methods of Collection

	@Override
	public int size() {
		int size = 0;
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	@Override
	public boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
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
			if (!(o1 == null ? o2 == null : o1.equals(o2)))
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
}

// package org.genericsystem.snapshot;
//
// import java.util.AbstractList;
// import java.util.Iterator;
//
// import org.genericsystem.core.Snapshot;
// import org.genericsystem.iterator.AbstractFilterIterator;
// import org.genericsystem.iterator.AbstractProjectionIterator;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// /**
// * @author Nicolas Feybesse
// *
// */
// public abstract class AbstractSnapshot<T> extends AbstractList<T> implements Snapshot<T> {
//
// protected static Logger log = LoggerFactory.getLogger(AbstractSnapshot.class);
//
// @Override
// public abstract Iterator<T> iterator();
//
// @Override
// public int size() {
// int size = 0;
// Iterator<T> iterator = iterator();
// while (iterator.hasNext()) {
// iterator.next();
// size++;
// }
// return size;
// }
//
// @Override
// public T get(int pos) {
// int i = 0;
// Iterator<T> iterator = iterator();
// while (iterator.hasNext()) {
// T next = iterator.next();
// if (pos == i++)
// return next;
// }
// return null;
// }
//
// @Override
// public boolean isEmpty() {
// return !iterator().hasNext();
// }
//
// @Override
// public Snapshot<T> filter(final Filter<T> filter) {
// return new AbstractSnapshot<T>() {
// @Override
// public Iterator<T> iterator() {
// return new AbstractFilterIterator<T>(AbstractSnapshot.this.iterator()) {
// @Override
// public boolean isSelected() {
// return filter.isSelected(next);
// }
// };
// }
// };
// }
//
// @Override
// public <E> Snapshot<E> project(final Projector<E, T> projector) {
// return new AbstractSnapshot<E>() {
// @Override
// public Iterator<E> iterator() {
// return new AbstractProjectionIterator<T, E>(AbstractSnapshot.this.iterator()) {
// @Override
// public E project(T t) {
// return projector.project(t);
// }
// };
// }
//
// };
// }
//
// @Override
// public void log() {
// log.info(toString());
// }
//
// }
