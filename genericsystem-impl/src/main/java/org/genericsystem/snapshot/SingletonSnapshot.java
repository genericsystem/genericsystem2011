package org.genericsystem.snapshot;

import java.util.Iterator;

public class SingletonSnapshot<T> implements FunctionalSnapshot<T> {
	private final T singleton;

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
