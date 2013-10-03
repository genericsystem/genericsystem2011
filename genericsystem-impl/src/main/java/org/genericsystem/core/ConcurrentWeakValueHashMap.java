package org.genericsystem.core;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;

public final class ConcurrentWeakValueHashMap<K, V> extends ConcurrentRefValueHashMap<K, V> {
	public ConcurrentWeakValueHashMap(Map<K, V> map) {
		super(map);
	}

	public ConcurrentWeakValueHashMap() {
		super();
	}

	public ConcurrentWeakValueHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	private static class MyWeakReference<K, T> extends WeakReference<T> implements MyReference<K, T> {
		private final K key;

		private MyWeakReference(K key, T referent, ReferenceQueue<T> q) {
			super(referent, q);
			this.key = key;
		}

		@Override
		public K getKey() {
			return key;
		}

		// MUST work with gced references too for the code in processQueue to work
		@Override
		public final boolean equals(final Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			final MyReference that = (MyReference) o;

			return key.equals(that.getKey()) && Objects.equals(get(), that.get());
		}

		@Override
		public final int hashCode() {
			return key.hashCode();
		}
	}

	@Override
	protected MyReference<K, V> createRef(K key, V value) {
		return new MyWeakReference<K, V>(key, value, myQueue);
	}
}
