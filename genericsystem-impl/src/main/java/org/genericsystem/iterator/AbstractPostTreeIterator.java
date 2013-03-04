package org.genericsystem.iterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractPostTreeIterator<T> extends AbstractAwareIterator<T> {

	protected Deque<T> stack = new ArrayDeque<>();
	protected Map<T, Iterator<T>> visited = new HashMap<>();

	public AbstractPostTreeIterator(T root) {
		stack.push(root);
	}

	protected abstract Iterator<T> children(T father);

	@Override
	protected void advance() {
		while (!stack.isEmpty()) {
			next = stack.peek();
			Iterator<T> iterator = visited.get(next);
			if (iterator == null)
				visited.put(next, iterator = children(next));
			if (!iterator.hasNext()) {
				stack.pop();
				return;
			}
			stack.push(iterator.next());
		}
		next = null;
	}
}
