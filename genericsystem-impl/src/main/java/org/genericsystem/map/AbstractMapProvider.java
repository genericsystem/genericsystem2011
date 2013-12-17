package org.genericsystem.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.iterator.AbstractProjectorAndFilterIterator;
import org.genericsystem.snapshot.AbstractSnapshot;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractMapProvider<Key extends Serializable, Value extends Serializable> extends GenericImpl implements MapProvider {

	static final String MAP_VALUE = "map";

	public static abstract class AbstractExtendedMap<K, V> extends AbstractMap<K, V> {
		abstract public Holder getValueHolder(K key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public AbstractExtendedMap<Key, Value> getExtendedMap(final Generic generic) {
		return new AbstractExtendedMap<Key, Value>() {

			@Override
			public Set<Map.Entry<Key, Value>> entrySet() {
				return new AbstractSnapshot<Entry<Key, Value>>() {
					@Override
					public Iterator<Entry<Key, Value>> iterator() {
						return entriesIterator(generic);
					}
				};
			}

			@Override
			public boolean containsKey(Object key) {
				return get(key) != null;
			}

			@Override
			public Value get(Object key) {
				Holder valueHolder = getValueHolder((Key) key);
				return valueHolder != null ? valueHolder.<Value> getValue() : null;
			}

			private Holder getMapHolder() {
				return ((GenericImpl) generic).<GenericImpl> getHolder(Statics.CONCRETE, (Holder) AbstractMapProvider.this);
			}

			private Holder getKeyHolder(Key key) {
				Holder mapHolder = getMapHolder();
				return mapHolder != null ? ((GenericImpl) mapHolder).getHolderByValue(Statics.CONCRETE, getKeyAttribute(key), key) : null;
			}

			@Override
			public Holder getValueHolder(Key key) {
				Holder keyHolder = getKeyHolder(key);
				return keyHolder != null ? keyHolder.getHolder(getValueAttribute()) : null;
			}

			private Attribute getValueAttribute() {
				return getCurrentCache().<Attribute> find(getValueAttributeClass());
			}

			@Override
			public Value put(Key key, Value value) {
				Value oldValue = get(key);
				Holder keyHolder = ((GenericImpl) generic).<GenericImpl> setHolder(AbstractMapProvider.this, MAP_VALUE).setHolder(getKeyAttribute(key), (Serializable) key);
				keyHolder.setHolder(getValueAttribute(), value);
				return oldValue;
			}

			private Iterator<Entry<Key, Value>> entriesIterator(final Generic generic) {
				return new InternalIterator<Entry<Key, Value>>() {
					@Override
					protected Map.Entry<Key, Value> project() {
						return new AbstractMap.SimpleImmutableEntry<Key, Value>(next.<Key> getValue(), valueHolder.<Value> getValue());
					}
				};
			}

			@Override
			public Set<Key> keySet() {
				return new AbstractSnapshot<Key>() {
					@Override
					public Iterator<Key> iterator() {
						return new InternalIterator<Key>();
					}
				};
			}

			private Iterator<Holder> getAllKeysIterator() {
				Holder map = generic.getHolder(AbstractMapProvider.this);
				return map == null ? Collections.<Holder> emptyIterator() : ((GenericImpl) map).<Holder> holdersIterator(getKeyAttribute(null));
			}

			class InternalIterator<T> extends AbstractProjectorAndFilterIterator<Holder, T> {

				public InternalIterator() {
					super(getAllKeysIterator());
				}

				protected Holder valueHolder;

				@Override
				public boolean isSelected() {
					valueHolder = next.getHolder(getValueAttribute());
					return valueHolder != null;
				}

				@Override
				protected T project() {
					return next.getValue();
				}

				@Override
				public void remove() {
					assert next.isAlive();
					Holder map = next.getBaseComponent();
					if (generic.equals(map.getBaseComponent()))
						next.remove();
					else
						put(next.<Key> getValue(), null);
				}

			}
		};
	}

	private Attribute getKeyAttribute(Key key) {
		return getCurrentCache().<Attribute> find(getKeyAttributeClass(key));
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass(Key key);

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();

}
