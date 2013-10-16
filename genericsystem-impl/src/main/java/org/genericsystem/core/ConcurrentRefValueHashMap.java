package org.genericsystem.core;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

abstract class ConcurrentRefValueHashMap<K, V> {

	private final ConcurrentHashMap<K, MyReference<K, V>> myMap = new ConcurrentHashMap<K, MyReference<K, V>>();
	protected final ReferenceQueue<V> myQueue = new ReferenceQueue<V>();

	protected interface MyReference<K, V> {
		K getKey();

		V get();
	}

	private void processQueue() {
		while (true) {
			@SuppressWarnings("unchecked")
			MyReference<K, V> ref = (MyReference<K, V>) myQueue.poll();
			if (ref == null)
				break;
			myMap.remove(ref.getKey(), ref);
		}
	}

	public V get(Object key) {
		MyReference<K, V> ref = myMap.get(key);
		if (ref == null)
			return null;
		return ref.get();
	}

	protected abstract MyReference<K, V> createRef(K key, V value);

	public V putIfAbsent(K key, V value) {
		MyReference<K, V> newRef = createRef(key, value);
		while (true) {
			processQueue();
			MyReference<K, V> oldRef = myMap.putIfAbsent(key, newRef);
			if (oldRef == null)
				return null;
			final V oldVal = oldRef.get();
			if (oldVal == null) {
				if (myMap.replace(key, oldRef, newRef))
					return null;
			} else {
				return oldVal;
			}
		}
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

	public static final class ConcurrentWeakValueHashMap<K, V> extends ConcurrentRefValueHashMap<K, V> {

		@Override
		protected MyReference<K, V> createRef(K key, V value) {
			return new MyWeakReference<K, V>(key, value, myQueue);
		}
	}

}