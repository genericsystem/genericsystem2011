package org.genericsystem.iterator;

import java.util.Iterator;

import org.genericsystem.snapshot.FunctionalSnapshot;

class SingletonSnapshot<T> implements FunctionalSnapshot<T> {
	private final T singleton;
	private final boolean hasNext = true;

	public SingletonSnapshot(T singleton) {
		assert singleton != null;
		this.singleton = singleton;
	}

	public T getSingleton() {
		return singleton;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

}