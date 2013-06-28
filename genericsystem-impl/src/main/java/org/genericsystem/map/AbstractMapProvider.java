package org.genericsystem.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
public abstract class AbstractMapProvider extends GenericImpl implements MapProvider {

	private static final String MAP_VALUE = "map";

	@Override
	public Map<Serializable, Serializable> getMap(final Generic generic) {
		return new AbstractMap<Serializable, Serializable>() {

			@Override
			public Set<Map.Entry<Serializable, Serializable>> entrySet() {
				return new AbstractSnapshot<Entry<Serializable, Serializable>>() {
					@Override
					public Iterator<Entry<Serializable, Serializable>> iterator() {
						return entriesIterator(generic);
					}
				};
			}

			@Override
			public boolean containsKey(Object key) {
				return get(key) != null;
			}

			@Override
			public Serializable get(Object key) {
				if (!(key instanceof Serializable))
					return null;
				GenericImpl map = generic.getHolder(AbstractMapProvider.this);
				if (map == null)
					return null;
				Holder keyHolder = map.getHolderByValue(getEngine().getCurrentCache().<Attribute> find(getKeyAttributeClass()), (Serializable) key);
				if (keyHolder == null)
					return null;
				return keyHolder.getHolder(getEngine().getCurrentCache().<Attribute> find(getValueAttributeClass())).getValue();
			}

			@Override
			public Serializable put(Serializable key, Serializable value) {
				assert null != value;
				Serializable oldValue = get(key);
				generic.setHolder(AbstractMapProvider.this, MAP_VALUE).setHolder(getEngine().getCurrentCache().<Attribute> find(getKeyAttributeClass()), key).setHolder(getEngine().getCurrentCache().<Attribute> find(getValueAttributeClass()), value);
				return oldValue;
			}
		};
	}

	private Iterator<Entry<Serializable, Serializable>> entriesIterator(final Generic generic) {
		Holder map = generic.getHolder(this);
		if (map == null)
			return Statics.emptyIterator();
		Attribute key = getEngine().getCurrentCache().<Attribute> find(getKeyAttributeClass());
		return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(((GenericImpl) map).<Holder> holdersIterator(key, getBasePos(key), false)) {

			@Override
			public boolean isSelected() {
				return true;
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
			protected Map.Entry<Serializable, Serializable> project() {
				return new AbstractMap.SimpleEntry<Serializable, Serializable>(next.getValue(), next.getHolder(getEngine().getCurrentCache().<Attribute> find(getValueAttributeClass())).getValue());
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();
}
