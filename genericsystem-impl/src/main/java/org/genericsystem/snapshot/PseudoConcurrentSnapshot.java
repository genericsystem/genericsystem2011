package org.genericsystem.snapshot;

import java.util.Iterator;
import org.genericsystem.core.Generic;
import org.genericsystem.iterator.AbstractGeneralAwareIterator;

/**
 * @author Nicolas Feybesse
 *
 */
public class PseudoConcurrentSnapshot implements AbstractSnapshot<Generic> {

	private Node head = null;
	private Node tail = null;

	@Override
	public boolean add(Generic element) {
		assert !this.contains(element);
		assert element != null;
		Node newNode = new Node(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
		return true;
	}

	public boolean remove(Generic element) {
		Iterator<Generic> iterator = iterator();
		while (iterator.hasNext())
			if (element.equals(iterator.next())) {
				iterator.remove();
				return true;
			}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return head == null;
	}

	@Override
	public Iterator<Generic> iterator() {
		return new InternalIterator();
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node, Generic> implements Iterator<Generic> {

		private Node last;

		@Override
		protected void advance() {
			last = next;
			next = next == null ? head : next.next;
		}

		@Override
		public Generic project() {
			return next.content;
		}

		@Override
		public void remove() {
			if (next == null)
				throw new IllegalStateException();
			if (last == null) {
				head = next.next;
				return;
			}
			last.next = next.next;
			if (next.next == null)
				tail = last;
		}
	}

	private static class Node {
		Generic content;
		Node next;

		private Node(Generic content) {
			this.content = content;
		}
	}

}
