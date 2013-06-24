package org.generic.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
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
	public Snapshot<Map.Entry<Serializable, Serializable>> getEntriesShot(final Cache cache, final Generic generic) {
		return new AbstractSnapshot<Map.Entry<Serializable, Serializable>>() {
			@Override
			public Iterator<Map.Entry<Serializable, Serializable>> iterator() {
				return entriesIterator(cache, generic);
			}

		};
	}

	@Override
	public Map<Serializable, Serializable> getMap(final Cache cache, final Generic generic) {
		return new AbstractMap<Serializable, Serializable>() {

			@Override
			public Set<Map.Entry<Serializable, Serializable>> entrySet() {
				return new AbstractSet<Map.Entry<Serializable, Serializable>>() {

					@Override
					public Iterator<Entry<Serializable, Serializable>> iterator() {
						return entriesIterator(cache, generic);
					}

					@Override
					public boolean isEmpty() {
						return !iterator().hasNext();
					}

					@Override
					public int size() {
						int i = 0;
						Iterator<Entry<Serializable, Serializable>> iterator = iterator();
						while (iterator.hasNext()) {
							i++;
							iterator.next();
						}
						return i;
					}
				};
			}

			// TODO implements get

			@Override
			public Serializable put(Serializable key, Serializable value) {
				assert null != value;
				Serializable oldValue = get(key);
				generic.setHolder(cache, AbstractMapProvider.this, MAP_VALUE).setHolder(cache, cache.<Attribute> find(getKeyAttributeClass()), key).setHolder(cache, cache.<Attribute> find(getValueAttributeClass()), value);
				return oldValue;
			}
		};
	}

	private Iterator<Entry<Serializable, Serializable>> entriesIterator(final Cache cache, final Generic generic) {
		Holder map = generic.getHolder(cache, this);
		if (map == null)
			return Statics.emptyIterator();
		return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(map.getHolders(cache, cache.<Attribute> find(getKeyAttributeClass())).iterator()) {

			@Override
			public boolean isSelected() {
				return true;
			}

			@Override
			public void remove() {
				assert next.isAlive(cache);
				Holder map = next.getBaseComponent();
				if (generic.equals(map.getBaseComponent()))
					next.remove(cache);
				else {
					map = generic.setHolder(cache, AbstractMapProvider.this, MAP_VALUE);
					if (!((GenericImpl) next.getBaseComponent()).equals(map))
						map.setHolder(cache, next, null);
				}
			}

			@Override
			protected Map.Entry<Serializable, Serializable> project() {
				return new AbstractMap.SimpleEntry<Serializable, Serializable>(next.getValue(), next.getHolder(cache, cache.<Attribute> find(getValueAttributeClass())).getValue());
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();
}
