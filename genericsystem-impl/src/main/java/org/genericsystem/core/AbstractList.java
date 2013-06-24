package org.genericsystem.core;

import java.util.Iterator;

public abstract class AbstractList<T> extends java.util.AbstractList<T> {

	@Override
	public abstract Iterator<T> iterator();

	@Override
	public T get(int index) {
		Iterator<T> ite = iterator();
		int i = 0;
		T next = null;
		while (ite.hasNext() && i <= index) {
			next = ite.next();
			i++;
		}
		return next;
	}

	@Override
	public int size() {
		Iterator<T> ite = iterator();
		int i = 0;
		while (ite.hasNext()) {
			ite.next();
			i++;
		}
		return i;
	}

}
