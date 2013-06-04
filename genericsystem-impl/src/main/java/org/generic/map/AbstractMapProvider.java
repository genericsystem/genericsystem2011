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
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.iterator.AbstractProjectorAndFilterIterator;

/**
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractMapProvider extends GenericImpl implements MapProvider {

	@Override
	public Map<Serializable, Serializable> getMap(final Cache cache, final Generic generic) {

		return new AbstractMap<Serializable, Serializable>() {

			final Generic base = generic;

			@Override
			public Set<Map.Entry<Serializable, Serializable>> entrySet() {
				return new AbstractSet<Map.Entry<Serializable, Serializable>>() {

					@Override
					public Iterator<Entry<Serializable, Serializable>> iterator() {
						return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(base.getHolders(cache, AbstractMapProvider.this).iterator()) {

							@Override
							public boolean isSelected() {
								return true;
							}

							@Override
							public void remove() {
								Holder holder = next;
								if (generic.equals(holder.getBaseComponent()))
									holder.remove(cache);
								else
									cancel(cache, holder, true);
							}

							@Override
							protected java.util.Map.Entry<Serializable, Serializable> project() {
								return next.getValue();
							}
						};
					}

					@Override
					public int size() {
						return base.getValues(cache, AbstractMapProvider.this).size();
					}

				};
			}

			@Override
			public Serializable put(Serializable key, Serializable value) {
				base.setHolder(cache, AbstractMapProvider.this, new AbstractMap.SimpleEntry<Serializable, Serializable>(key, value));
				return null;
			}
		};
	}
}
