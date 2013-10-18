//package org.genericsystem.iterator;
//
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.HashMap;
//import java.util.Iterator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author Nicolas Feybesse
// * 
// * @param <T>
// */
//public abstract class AbstractSelectablePostTreeIterator2<T> extends AbstractAwareIterator<T> {
//
//	protected static Logger log = LoggerFactory.getLogger(AbstractSelectablePostTreeIterator.class);
//	protected Deque<T> stack = new ArrayDeque<>();
//	protected SelectableIterators iterators = new SelectableIterators();
//
//	public AbstractSelectablePostTreeIterator2(T root) {
//		stack.push(root);
//	}
//
//	protected abstract Iterator<T> children(T node);
//
//	@Override
//	protected void advance() {
//		while (!stack.isEmpty()) {
//			next = stack.peek();
//			SelectableIterator<T> iterator = iterators.get(next);
//			if (!iterator.hasNext()) {
//				stack.pop();
//				if (!iterator.isSelectable())
//					iterators.unselect(stack.peek());
//				else if (isSelectable()) {
//					iterator.unselect();
//					iterators.unselect(stack.peek());
//					return;
//				}
//			} else
//				stack.push(iterator.next());
//		}
//		next = null;
//	}
//
//	protected abstract boolean isSelectable();
//
//	protected static class SelectableIterator<T> implements Iterator<T> {
//		private boolean selectable = true;
//		private Iterator<T> iterator;
//
//		public SelectableIterator(Iterator<T> iterator) {
//			this.iterator = iterator;
//		}
//
//		public boolean isSelectable() {
//			return selectable;
//		}
//
//		public void unselect() {
//			selectable = false;
//		}
//
//		@Override
//		public boolean hasNext() {
//			return iterator.hasNext();
//		}
//
//		@Override
//		public T next() {
//			return iterator.next();
//		}
//
//		@Override
//		public void remove() {
//			iterator.remove();
//		}
//	}
//
//	protected class SelectableIterators extends HashMap<T, SelectableIterator<T>> {
//
//		private static final long serialVersionUID = 5176111143275422965L;
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public SelectableIterator<T> get(Object key) {
//			assert key != null;
//			SelectableIterator<T> iterator = super.get(key);
//			if (iterator == null)
//				put((T) key, iterator = new SelectableIterator<>(children((T) key)));
//			return iterator;
//		}
//
//		public void unselect(T key) {
//			if (key != null)
//				super.get(key).unselect();
//		}
//	}
// }
