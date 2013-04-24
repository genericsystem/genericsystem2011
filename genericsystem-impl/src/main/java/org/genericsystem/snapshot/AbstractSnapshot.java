package org.genericsystem.snapshot;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import org.genericsystem.core.Snapshot;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSnapshot<T> implements Snapshot<T> {

	protected static Logger log = LoggerFactory.getLogger(AbstractSnapshot.class);

	@Override
	public abstract Iterator<T> iterator();

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
	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	@Override
	public boolean contains(Object object) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext())
			if (Objects.equals(object, iterator.next()))
				return true;
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	public boolean containsAll(Snapshot<?> c) {
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
	public int hashCode() {
		int hashCode = 1;
		for (T t : this)
			hashCode = 31 * hashCode + (t == null ? 0 : t.hashCode());
		return hashCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Snapshot))
			return false;
		Iterator<T> it = ((Snapshot<T>) obj).iterator();
		for (T t : this)
			if (!it.hasNext() || !it.next().equals(t))
				return false;
		return !it.hasNext();
	}

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

	@Override
	public T first() {
		Iterator<T> it = iterator();
		return it.hasNext() ? it.next() : null;
	}
}
