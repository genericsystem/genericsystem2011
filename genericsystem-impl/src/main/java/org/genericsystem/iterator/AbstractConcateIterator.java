package org.genericsystem.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Nicolas Feybesse
 * 
 * @param <U>
 * @param <T>
 */
public abstract class AbstractConcateIterator<U, T> extends AbstractAwareIterator<T> implements Iterator<T> {

	private Iterator<U> elements;
	private Iterator<T> iterator = Collections.emptyIterator();

	private Set<T> alreadyAdded = new HashSet<>();

	public AbstractConcateIterator(Iterator<U> elements) {
		this.elements = elements;
	}

	protected abstract Iterator<T> getIterator(U element);

	@Override
	protected void advance() {
		for (;;) {
			if (iterator.hasNext()) {
				next = iterator.next();
				if (!alreadyAdded.add(next))
					continue;
				return;
			}
			if (!elements.hasNext()) {
				next = null;
				return;
			}
			iterator = getIterator(elements.next());
		}
	}

	public static class ConcateIterator<T> extends AbstractConcateIterator<Iterator<T>, T> {

		@SafeVarargs
		public ConcateIterator(Iterator<T>... iterators) {
			super(Arrays.asList(iterators).iterator());
		}

		@Override
		protected Iterator<T> getIterator(Iterator<T> element) {
			return element;
		}

	}
}
