package org.genericsystem.core;

import java.util.List;
import java.util.Set;

/**
 * The Snaphot is an aware iterable of the graphe.
 * 
 * @author Nicolas Feybesse
 * 
 */
public interface Snapshot<T> extends List<T>, Set<T> {

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
