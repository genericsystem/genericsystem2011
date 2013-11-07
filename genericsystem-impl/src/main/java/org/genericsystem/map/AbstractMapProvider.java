package org.genericsystem.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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

	public static abstract class AbstractExtendedMap<K, V> extends AbstractMap<K, V> implements ExtendedMap<K, V> {
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExtendedMap<Key, Value> getExtendedMap(final Generic generic) {
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
				Holder valueHolder = getValueHolder((Serializable) key);
				return valueHolder != null ? valueHolder.<Value> getValue() : null;
			}

			@Override
			public Holder getValueHolder(Serializable key) {
				Holder keyHolder = getKeyHolder(key);
				return keyHolder != null ? keyHolder.getHolder(Statics.CONCRETE, getValueAttribute()) : null;
			}

			private Holder getKeyHolder(Serializable key) {
				GenericImpl map = generic.getHolder(Statics.CONCRETE, AbstractMapProvider.this);
				return map != null ? map.getHolderByValue(Statics.CONCRETE, getKeyAttribute(), key) : null;
			}

			@Override
			public Value put(Key key, Value value) {
				assert null != value;
				Value oldValue = get(key);
				if (Objects.equals(oldValue, value))
					return oldValue;
				Holder attribute = getRealKeyAttribute(key);
				Holder keyHolder = generic.<GenericImpl> setHolder(AbstractMapProvider.this, MAP_VALUE).setHolder(getSpecializationClass(key), attribute, (Serializable) key);
				keyHolder.setHolder(getValueAttribute(), value);
				return oldValue;
			}

			@Override
			public Set<Key> keySet() {
				return new AbstractSnapshot<Key>() {
					@Override
					public Iterator<Key> iterator() {
						Holder map = generic.getHolder(Statics.CONCRETE, AbstractMapProvider.this);
						if (map == null)
							return Collections.emptyIterator();
						Attribute key = getKeyAttribute();
						return new AbstractProjectorAndFilterIterator<Holder, Key>(((GenericImpl) map).<Holder> holdersIterator(key, Statics.CONCRETE, getBasePos(key), false)) {

							@Override
							public boolean isSelected() {
								return next.getHolder(Statics.CONCRETE, getValueAttribute()) != null;
							}

							@Override
							protected Key project() {
								return next.getValue();
							}
						};
					}
				};
			}
		};
	}

	protected <T extends GenericImpl> Class<T> getSpecializationClass(Key key) {
		return null;
	};

	private Iterator<Entry<Key, Value>> entriesIterator(final Generic generic) {
		Holder map = generic.getHolder(Statics.CONCRETE, this);
		if (map == null)
			return Collections.emptyIterator();
		Attribute key = getKeyAttribute();
		return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Key, Value>>(((GenericImpl) map).<Holder> holdersIterator(key, Statics.CONCRETE, getBasePos(key), false)) {

			private Holder valueHolder;

			@Override
			public boolean isSelected() {
				valueHolder = next.getHolder(Statics.CONCRETE, getValueAttribute());
				return valueHolder != null;
			}

			@Override
			public void remove() {
				assert next.isAlive();
				Holder map = next.getBaseComponent();
				if (generic.equals(map.getBaseComponent()))
					next.remove();
				else {
					map = generic.setHolder(AbstractMapProvider.this, MAP_VALUE);
					if (!((GenericImpl) next.getBaseComponent()).equals(map))
						map.setHolder(next, null);
				}
			}

			@Override
			protected Map.Entry<Key, Value> project() {
				return new AbstractMap.SimpleImmutableEntry<Key, Value>(next.<Key> getValue(), valueHolder.<Value> getValue());
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();

	private Attribute getKeyAttribute() {
		return getCurrentCache().<Attribute> find(getKeyAttributeClass());
	}

	private Attribute getRealKeyAttribute(Key key) {
		if (key instanceof AxedPropertyClass) {
			Attribute attribute = getCurrentCache().find(((AxedPropertyClass) key).getClazz());
			// TODO kk ?
			if (attribute.<AxedPropertyClass> getValue().getAxe() == ((AxedPropertyClass) key).getAxe())
				return attribute;
		}
		return getKeyAttribute();
	}

	private Attribute getValueAttribute() {
		return getCurrentCache().<Attribute> find(getValueAttributeClass());
	}
}
