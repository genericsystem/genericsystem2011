package org.genericsystem.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.core.Statics.Primaries;
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

	@SuppressWarnings("unchecked")
	@Override
	public ExtendedMap<Key, Value> getMap(final Generic generic) {
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
				return (Value) (valueHolder != null ? valueHolder.getValue() : null);
			}

			@Override
			public Holder getValueHolder(Serializable key) {
				Holder keyHolder = getKeyHolder(key);
				if (keyHolder == null)
					return null;
				return keyHolder.getHolder(getCurrentCache().<Attribute> find(getValueAttributeClass()));
			}

			// @Override
			private Holder getKeyHolder(Serializable key) {
				GenericImpl map = generic.getHolder(AbstractMapProvider.this);
				if (map == null)
					return null;
				return map.getHolderByValue(getCurrentCache().<Attribute> find(getKeyAttributeClass()), key);
			}

			@Override
			public Value put(Key key, Value value) {
				assert null != value;
				Value oldValue = get(key);
				Holder keyHolder = generic.setHolder(AbstractMapProvider.this, MAP_VALUE).setHolder(getCurrentCache().<Attribute> find(getKeyAttributeClass()), (Serializable) key);
				setSingularHolder(keyHolder, getCurrentCache().<Attribute> find(getValueAttributeClass()), (Serializable) value);
				return oldValue;
			}
		};
	}

	// TODO KK code copier du setHolder
	private static <T extends Holder> T setSingularHolder(Holder keyHolder, Holder attribute, Serializable value, Generic... targets) {
		int basePos = keyHolder.getBasePos(attribute);
		T holder = keyHolder.getHolder((Attribute) attribute, basePos);
		Generic implicit = ((GenericImpl) attribute).bindPrimary(keyHolder.getClass(), value, SystemGeneric.CONCRETE, true);
		if (holder == null)
			return null != value ? ((GenericImpl) keyHolder).<T> bind(null, implicit, attribute, basePos, true, targets) : null;
		if (!keyHolder.equals(holder.getComponent(basePos))) {
			if (value == null)
				return keyHolder.cancel(holder, basePos, true);
			if (!(((GenericImpl) holder).equiv(new Primaries(implicit, attribute).toArray(), Statics.insertIntoArray(holder.getComponent(basePos), targets, basePos))))
				keyHolder.cancel(holder, basePos, true);
			return ((GenericImpl) keyHolder).<T> bind(null, implicit, attribute, basePos, true, targets);
		}
		if (((GenericImpl) holder).equiv(new Primaries(implicit, attribute).toArray(), Statics.insertIntoArray(keyHolder, targets, basePos)))
			return holder;
		holder.remove();
		return setSingularHolder(keyHolder, attribute, value, targets);
	}

	private Iterator<Entry<Key, Value>> entriesIterator(final Generic generic) {
		Holder map = generic.getHolder(this);
		if (map == null)
			return Statics.emptyIterator();
		Attribute key = getCurrentCache().<Attribute> find(getKeyAttributeClass());
		return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Key, Value>>(((GenericImpl) map).<Holder> holdersIterator(key, getBasePos(key), false)) {

			private Holder valueHolder;

			@Override
			public boolean isSelected() {
				valueHolder = next.getHolder(getCurrentCache().<Attribute> find(getValueAttributeClass()));
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

			@SuppressWarnings("unchecked")
			@Override
			protected Map.Entry<Key, Value> project() {
				return new AbstractMap.SimpleEntry<Key, Value>((Key) next.getValue(), valueHolder.<Value> getValue());
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();
}
