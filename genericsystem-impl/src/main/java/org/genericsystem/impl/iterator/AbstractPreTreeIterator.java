package org.genericsystem.impl.iterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;

public abstract class AbstractPreTreeIterator<T> extends HashSet<T> implements Iterator<T> {

	private static final long serialVersionUID = -518282246760045090L;

	protected Deque<Iterator<T>> deque = new ArrayDeque<Iterator<T>>();

	public AbstractPreTreeIterator(T rootNode) {
		deque.push(new SingletonIterator<T>(rootNode));
	}

	@Override
	public boolean hasNext() {
		return (!deque.isEmpty() && deque.peek().hasNext());
	}

	public abstract Iterator<T> children(T node);

	@Override
	public T next() {
		Iterator<T> iterator = deque.peek();
		final T node = iterator.next();
		if (!iterator.hasNext())
			deque.pop();
		Iterator<T> children = new AbstractFilterIterator<T>(children(node)) {
			@Override
			public boolean isSelected() {
				return add(next);
			}
		};
		if (children.hasNext())
			deque.push(children);
		return node;
	}

	@Override
	public void remove() {
		throw new IllegalStateException();
	}
}
