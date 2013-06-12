//package org.genericsystem.snapshot;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//
///**
// * @author Nicolas Feybesse
// * 
// */
//public abstract class AbstractSequentiableSnapshot<T> extends AbstractSnapshot<T> implements List<T> {
//
//	private static final int INITIAL_VALUE = -1;
//
//	private Iterator<T> previousIterator;
//
//	private int currentIndex = INITIAL_VALUE;
//
//	private T currentItem;
//
//	@Override
//	public T get(int index) {
//		if (index != currentIndex)
//			currentItem = null;
//		if (index < currentIndex)
//			reInit();
//		return getItem(index);
//	}
//
//	private void reInit() {
//		previousIterator = null;
//		currentIndex = INITIAL_VALUE;
//		currentItem = null;
//	}
//
//	private Iterator<T> getIterator() {
//		if (previousIterator == null)
//			previousIterator = iterator();
//		return previousIterator;
//	}
//
//	private T getItem(int index) {
//		if (currentItem == null) {
//			while (currentIndex < index) {
//				currentItem = getIterator().next();
//				currentIndex++;
//			}
//		}
//		return currentItem;
//	}
//
//	public abstract Iterator<T> sequentiableIterator();
//
//	@Override
//	public Iterator<T> iterator() {
//		reInit();
//		return sequentiableIterator();
//	}
//
//	@Override
//	public Object[] toArray() {
//		int i = 0;
//		Object[] array = new Object[size()];
//		Iterator<T> it = iterator();
//		while (it.hasNext())
//			array[i++] = it.next();
//		return array;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public <U> U[] toArray(U[] array) {
//		int i = 0;
//		Iterator<T> it = iterator();
//		while (it.hasNext())
//			array[i++] = (U) it.next();
//		return array;
//	}
//
//	@Override
//	public boolean add(T e) {
//		throw new IllegalStateException();
//	}
//
//	@Override
//	public boolean remove(Object o) {
//		throw new IllegalStateException();
//	}
//
//	@Override
//	public boolean containsAll(Collection<?> c) {
//		for (Object e : c)
//			if (!contains(e))
//				return false;
//		return true;
//	}
//
//	@Override
//	public boolean addAll(Collection<? extends T> c) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public void clear() {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public boolean addAll(int index, Collection<? extends T> c) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public T set(int index, T element) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public void add(int index, T element) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public T remove(int index) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public int indexOf(Object o) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public int lastIndexOf(Object o) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public ListIterator<T> listIterator() {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public ListIterator<T> listIterator(int index) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public List<T> subList(int fromIndex, int toIndex) {
//		throw new UnsupportedOperationException();
//	}
//
// }
