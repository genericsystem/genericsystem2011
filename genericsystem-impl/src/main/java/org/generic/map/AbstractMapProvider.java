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
						return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(generic.getHolders(cache, AbstractMapProvider.this).iterator()) {

							@Override
							public boolean isSelected() {
								return true;
							}

							@Override
							public void remove() {
								Holder holder = next;
								if (generic.equals(holder.getBaseComponent())) {
									// holder.remove(cache);
									generic.clearAll(cache, AbstractMapProvider.this, true);
									// Holder phantom = ((GenericImpl) generic).getHolderByValue(cache, AbstractMapProvider.this, null);
									// if (phantom != null && generic.equals(phantom.getBaseComponent()))
									// phantom.remove(cache);
								} else
									cancel(cache, holder, true);
							}

							@Override
							protected Map.Entry<Serializable, Serializable> project() {
								return next.getValue();
							}
						};
					}

					@Override
					public int size() {
						return generic.getValues(cache, AbstractMapProvider.this).size();
					}
				};
			}

			@Override
			public Serializable put(Serializable key, Serializable value) {
				generic.setHolder(cache, AbstractMapProvider.this, new AbstractMap.SimpleEntry<Serializable, Serializable>(key, value));
				return null;
			}
		};
	}
}
