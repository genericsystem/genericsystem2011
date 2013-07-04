package org.genericsystem.snapshot;

import java.util.AbstractList;
import java.util.Iterator;

import org.genericsystem.core.Snapshot;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSnapshot<T> extends AbstractList<T> implements Snapshot<T> {

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

}
