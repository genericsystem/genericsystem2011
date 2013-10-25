package org.genericsystem.core;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.genericsystem.core.EngineImpl.RootTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class HomeTreeNode implements Comparable<HomeTreeNode> {

	protected static Logger log = LoggerFactory.getLogger(HomeTreeNode.class);

	HomeTreeNode metaNode;
	Serializable value;
	long ts;

	private ConcurrentWeakValueHashMap<Serializable, HomeTreeNode> instancesNodes = new ConcurrentWeakValueHashMap<>();

	protected HomeTreeNode(HomeTreeNode metaNode, Serializable value) {
		this.metaNode = metaNode == null ? this : metaNode;
		this.value = value;
		ts = getHomeTree().pickNewTs();
	}

	protected HomeTreeNode(long ts, HomeTreeNode metaNode, Serializable value) {
		this.metaNode = metaNode == null ? this : metaNode;
		this.value = value;
		this.ts = ts;
	}

	public HomeTreeNode findInstanceNode(Serializable value) {
		if (value == null)
			value = NULL_VALUE;
		return instancesNodes.get(value);
	}

	private static final String NULL_VALUE = "NULL_VALUE";

	public HomeTreeNode bindInstanceNode(Serializable value) {
		assert getMetaLevel() <= 1 : this + " " + value;
		if (value == null)
			value = NULL_VALUE;
		HomeTreeNode result = findInstanceNode(value);
		if (result != null)
			return result;
		HomeTreeNode newHomeTreeNode = new HomeTreeNode(this, value);
		result = instancesNodes.putIfAbsent(value, newHomeTreeNode);
		return result == null ? newHomeTreeNode : result;
	}

	public HomeTreeNode bindInstanceNode(long ts, Serializable value) {
		if (value == null)
			value = NULL_VALUE;
		HomeTreeNode result = findInstanceNode(value);
		if (result != null)
			return result;
		HomeTreeNode newHomeTreeNode = new HomeTreeNode(ts, this, value);
		result = instancesNodes.putIfAbsent(value, newHomeTreeNode);
		return result == null ? newHomeTreeNode : result;
	}

	public RootTreeNode getHomeTree() {
		return metaNode.getHomeTree();
	}

	public boolean isRoot() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <S extends Serializable> S getValue() {
		return value == NULL_VALUE ? null : (S) value;
	}

	public int getMetaLevel() {
		assert 1 + metaNode.getMetaLevel() >= 1;
		return 1 + metaNode.getMetaLevel();
	}

	public boolean inheritsFrom(HomeTreeNode homeTreeNode) {
		return this.equals(homeTreeNode) ? true : metaNode.inheritsFrom(homeTreeNode);
	}

	@Override
	public String toString() {
		return metaNode.toString() + "|" + getValue();
	}

	@Override
	public int compareTo(HomeTreeNode homeTreeNode) {
		return Long.compare(ts, homeTreeNode.ts);
	}

	private static class ConcurrentWeakValueHashMap<K, V> {

		private final ConcurrentHashMap<K, KeyValueRef<K, V>> map = new ConcurrentHashMap<K, KeyValueRef<K, V>>();
		protected final ReferenceQueue<V> queue = new ReferenceQueue<V>();

		protected interface KeyValueRef<K, V> {
			K getKey();

			V get();
		}

		private void processQueue() {
			while (true) {
				@SuppressWarnings("unchecked")
				KeyValueRef<K, V> ref = (KeyValueRef<K, V>) queue.poll();
				if (ref == null)
					break;
				map.remove(ref.getKey(), ref);
			}
		}

		public V get(Object key) {
			KeyValueRef<K, V> ref = map.get(key);
			if (ref == null)
				return null;
			return ref.get();
		}

		protected KeyValueRef<K, V> createRef(K key, V value) {
			return new KeyValueWeakRef<K, V>(key, value, queue);
		}

		public V putIfAbsent(K key, V value) {
			KeyValueRef<K, V> newRef = createRef(key, value);
			while (true) {
				processQueue();
				KeyValueRef<K, V> oldRef = map.putIfAbsent(key, newRef);
				if (oldRef == null)
					return null;
				final V oldVal = oldRef.get();
				if (oldVal == null) {
					if (map.replace(key, oldRef, newRef))
						return null;
				} else {
					return oldVal;
				}
			}
		}

		private static class KeyValueWeakRef<K, T> extends WeakReference<T> implements KeyValueRef<K, T> {
			private final K key;

			private KeyValueWeakRef(K key, T referent, ReferenceQueue<T> q) {
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
				final KeyValueRef<?, ?> that = (KeyValueRef<?, ?>) o;
				return key.equals(that.getKey()) && Objects.equals(get(), that.get());
			}

			@Override
			public final int hashCode() {
				return key.hashCode();
			}
		}
	}

}
