package org.genericsystem.web.util;
//package org.genericsystem.web.util;
//
//import java.util.AbstractList;
//import java.util.Iterator;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public abstract class AbstractSequentialList<E> extends AbstractList<E> {
//
//	protected AbstractSequentialList() {
//	}
//
//	@Override
//	public E get(int index) {
//		Iterator<E> iterator = iterator();
//		E next = null;
//		for (int i = 0; i <= index; i++)
//			if (iterator.hasNext())
//				next = iterator.next();
//			else
//				throw new IndexOutOfBoundsException("Index: " + index);
//		return next;
//	}
//
//	@Override
//	public int size() {
//		int size = 0;
//		Iterator<E> iterator = iterator();
//		while (iterator.hasNext()) {
//			iterator.next();
//			size++;
//		}
//		return size;
//	}
//
//	@Override
//	public abstract Iterator<E> iterator();
//
// }
