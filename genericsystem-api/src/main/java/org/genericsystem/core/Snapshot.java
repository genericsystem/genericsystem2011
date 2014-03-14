package org.genericsystem.core;

/**
 * The Snaphot is an aware iterable of the graphe.
 * 
 * @author Nicolas Feybesse
 * 
 */
public interface Snapshot<T> {

	/**
	 * Filter the Snapshot.
	 * 
	 * @param filter
	 *            The Filter.
	 * @see Filter
	 * @return The filter Snapshot.
	 */
	// Snapshot<T> filter(Filter<T> filter);

	/**
	 * Project the Snapshot.
	 * 
	 * @param projector
	 *            The Projecter.
	 * @return The project Snapshot.
	 */
	// <E> Snapshot<E> project(Projector<E, T> projector);

	void log2();

	// @Override
	// default Spliterator<T> spliterator() {
	// return Set.super.spliterator();
	// }
}
