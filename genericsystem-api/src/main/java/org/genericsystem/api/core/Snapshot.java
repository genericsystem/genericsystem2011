package org.genericsystem.api.core;

import java.util.Collection;

/**
 * The Snaphot is a conscious iterable.
 * 
 * @author Nicolas Feybesse
 * 
 */
public interface Snapshot<T> extends Iterable<T> {

	/**
	 * Returns the size.
	 * 
	 * @return The size.
	 */
	int size();

	/**
	 * Returns element of the index.
	 * 
	 * @param index
	 *            The index.
	 * @return The element.
	 */
	T get(int index);

	/**
	 * Returns the fisrt element.
	 * 
	 * @return The element.
	 */
	T first();

	/**
	 * Check if the Snapshot is empty.
	 * 
	 * @return True if empty.
	 */
	boolean isEmpty();

	/**
	 * Check if the Snapshot contains the object.
	 * 
	 * @param object
	 *            The object.
	 * @return True if the Snapshot contains the object.
	 */
	boolean contains(Object object);

	/**
	 * Check if the Snapshot contains the objects.
	 * 
	 * @param c
	 *            Objects Collection.
	 * @return True if the Snapshot contains the objects.
	 */
	boolean containsAll(Collection<?> c);

	/**
	 * Check if the Snapshot contains the objects.
	 * 
	 * @param c
	 *            Objects Snapshot.
	 * @return True if the Snapshot contains the objects.
	 */
	boolean containsAll(Snapshot<?> c);

	/**
	 * Filter the Snapshot.
	 * 
	 * @param filter
	 *            The Filter.
	 * @see Filter
	 * @return The filter Snapshot.
	 */
	Snapshot<T> filter(Filter<T> filter);

	/**
	 * Project the Snapshot.
	 * 
	 * @param projector
	 *            The Projecter.
	 * @return The project Snapshot.
	 */
	<E> Snapshot<E> project(Projector<E, T> projector);

	/**
	 * Filter.
	 * 
	 * @author Nicolas Feybesse
	 */
	static interface Filter<T> {
		/**
		 * Returns true if element is selected.
		 * 
		 * @param element
		 *            The element.
		 * @return True if element is selected.
		 */
		boolean isSelected(T element);
	}

	/**
	 * Projector.
	 * 
	 * @author Nicolas Feybesse
	 */
	static interface Projector<T, E> {
		/**
		 * Returns project element.
		 * 
		 * @param element
		 *            The initial element.
		 * @return The project element.
		 */
		T project(E element);
	}

	void log();
}
