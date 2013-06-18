package org.generic.map;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.genericsystem.iterator.AbstractProjectorAndFilterIterator;
import org.genericsystem.snapshot.AbstractSnapshot;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractMapProvider extends GenericImpl implements MapProvider {

	@Override
	public Snapshot<Map.Entry<Serializable, Serializable>> getEntriesShot(final Cache cache, final Generic generic) {

		return new AbstractSnapshot<Map.Entry<Serializable, Serializable>>() {

			@Override
			public Iterator<Map.Entry<Serializable, Serializable>> iterator() {
				return new AbstractProjectionIterator<Holder, Map.Entry<Serializable, Serializable>>(generic.getHolders(cache, AbstractMapProvider.this).iterator()) {
					@Override
					public Map.Entry<Serializable, Serializable> project(Holder holder) {
						return holder.getValue();
					}
				};
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
						Holder map = generic.getHolder(cache, cache.<Attribute> find(PropertiesMapProvider.class));
						if (map == null)
							return Statics.emptyIterator();
						return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(map.getHolders(cache, cache.<Attribute> find(getKeyAttributeClass())).iterator()) {
							Holder holder;

							@Override
							public boolean isSelected() {
								holder = next.getHolder(cache, cache.<Attribute> find(getValueAttributeClass()));
								return holder != null;
							}

							@Override
							public void remove() {
								Holder map = next.getBaseComponent();
								if (generic.equals(map.getBaseComponent()))
									next.remove(cache);
								else
									generic.setHolder(cache, cache.<Attribute> find(PropertiesMapProvider.class), PropertiesMapProvider.NAME).setHolder(cache, cache.<Attribute> find(getKeyAttributeClass()), next.getValue())
											.setHolder(cache, cache.<Attribute> find(getValueAttributeClass()), null);
							}

							@Override
							protected Map.Entry<Serializable, Serializable> project() {
								return new AbstractMap.SimpleEntry<Serializable, Serializable>(next.getValue(), holder.getValue());
							}
						};
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
				Serializable oldValue = get(key);
				generic.setHolder(cache, cache.<Attribute> find(PropertiesMapProvider.class), PropertiesMapProvider.NAME).setHolder(cache, cache.<Attribute> find(getKeyAttributeClass()), key)
						.setHolder(cache, cache.<Attribute> find(getValueAttributeClass()), value);
				return oldValue;
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();
}
