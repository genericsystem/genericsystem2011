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
						return new AbstractProjectorAndFilterIterator<Holder, Map.Entry<Serializable, Serializable>>(generic.getHolders(cache, cache.<Attribute> find(getKeyAttributeClass())).iterator()) {
							Holder holder;

							@Override
							public boolean isSelected() {
								holder = next.getHolder(cache, cache.<Attribute> find(getValueAttributeClass()));
								return holder != null;
							}

							@Override
							public void remove() {
								if (generic.equals(next.getBaseComponent())) {
									if (holder != null) {
										assert next.equals(holder.getBaseComponent());
										((GenericImpl) holder).internalClearAll(cache, cache.<Attribute> find(getValueAttributeClass()), getBasePos(AbstractMapProvider.this), true, next.getValue());
										// else
										// next.cancel(cache, next, true);
									}
									((GenericImpl) generic).internalClearAll(cache, cache.<Attribute> find(getKeyAttributeClass()), getBasePos(AbstractMapProvider.this), true, next.getValue());
								} else
									generic.cancel(cache, next, true);
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
				generic.setHolder(cache, cache.<Attribute> find(getKeyAttributeClass()), key).setHolder(cache, cache.<Attribute> find(getValueAttributeClass()), value);
				return oldValue;
			}
		};
	}

	public abstract <T extends Attribute> Class<T> getKeyAttributeClass();

	public abstract <T extends Attribute> Class<T> getValueAttributeClass();

	public abstract class AbstractKeyAttribute extends GenericImpl implements Attribute {

	}

	public abstract class AbstractValueAttribute extends GenericImpl implements Attribute {

	}
}
