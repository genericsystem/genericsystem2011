package org.genericsystem.impl.iterator;

import java.util.Iterator;

public abstract class AbstractPreTreeLeafIterator<T> extends AbstractPreTreeIterator<T> {

	public AbstractPreTreeLeafIterator(T rootNode) {
		super(rootNode);
	}

	@Override
	public T next() {
		for (;;) {
			Iterator<T> iterator = deque.peek();
			T node = iterator.next();
			assert node != null;
			if (!iterator.hasNext())
				deque.pop();
			Iterator<T> children = children(node);
			if (children.hasNext())
				deque.push(children);
			else
				return node;
		}
	}

}