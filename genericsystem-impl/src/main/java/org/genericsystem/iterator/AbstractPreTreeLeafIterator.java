package org.genericsystem.iterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Nicolas Feybesse
 * 
 * @param <T>
 */
public abstract class AbstractPreTreeLeafIterator<T> extends AbstractAwareIterator<T> {

	private Set<T> alreadyTraversed = new HashSet<>();
	protected Deque<Iterator<T>> deque = new ArrayDeque<Iterator<T>>();

	public AbstractPreTreeLeafIterator(T rootNode) {
		deque.push(new SingletonIterator<T>(rootNode));
	}

	@Override
	public boolean hasNext() {
		return (!deque.isEmpty() && deque.peek().hasNext());
	}

	@Override
	protected void advance() {
		while (!deque.isEmpty() && deque.peek().hasNext()) {
			Iterator<T> iterator = deque.peek();
			final T node = iterator.next();
			if (!iterator.hasNext())
				deque.pop();
			Iterator<T> children = new AbstractFilterIterator<T>(children(node)) {
				@Override
				public boolean isSelected() {
					return alreadyTraversed.add(next);
				}
			};
			next = node;
			if (children.hasNext())
				deque.push(children);
			else
				return;
		}
		next = null;

	}

	public abstract Iterator<T> children(T node);

	@Override
	public void remove() {
		throw new IllegalStateException();
	}
}
