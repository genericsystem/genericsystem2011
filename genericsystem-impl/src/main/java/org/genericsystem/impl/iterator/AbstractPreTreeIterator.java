package org.genericsystem.impl.iterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public abstract class AbstractPreTreeIterator<T> implements Iterator<T> {

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
		T node = iterator.next();
		if (!iterator.hasNext())
			deque.pop();
		Iterator<T> children = children(node);
		if (children.hasNext())
			deque.push(children);
		return node;
	}

	@Override
	public void remove() {
		throw new IllegalStateException();
	}
}
