package org.genericsystem.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.genericsystem.core.Statics.Components;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.core.Statics.Supers;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.FunctionalConsistencyViolationException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * 
 */
class Vertex {
	private final CacheImpl cache;
	private HomeTreeNode homeTreeNode;
	private HomeTreeNode[] primaries;
	private Generic[] components;
	private Generic[] supers;

	Vertex(CacheImpl cache, HomeTreeNode homeTreeNode, Generic[] supers, Generic[] aliveNullComponents) {
		this.cache = cache;
		this.homeTreeNode = homeTreeNode;
		primaries = new Primaries(homeTreeNode, supers).toArray();
		components = aliveNullComponents;
		this.supers = supers;
	}

	HomeTreeNode getHomeTreeNode() {
		return homeTreeNode;
	}

	HomeTreeNode[] getPrimaries() {
		return primaries;
	}

	Generic[] getComponents() {
		return components;
	}

	Generic[] getSupers() {
		return supers;
	}

	public <T extends Generic> T muteAndFind(Generic meta, boolean isProperty, boolean isSingular, int basePos, boolean existsException) {
		computeSupersAndMute(meta, isProperty, isSingular, basePos);
		return findInSupers(existsException);
	}

	void computeSupersAndMute(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos) {
		supers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos);
		for (Generic directSuper : supers) {
			primaries = new Primaries(directSuper, primaries).toArray();
			components = new Components(components, ((GenericImpl) directSuper).components).toArray();
		}
	}

	@SuppressWarnings({ "unchecked" })
	private <T extends Generic> T findInSupers(boolean existsException) throws RollbackException {
		for (Generic directSuper : supers) {
			if (((GenericImpl) directSuper).equiv(primaries, components))
				if (supers.length == 1 && homeTreeNode.equals(((GenericImpl) directSuper).homeTreeNode))
					if (existsException)
						cache.rollback(new ExistsException(directSuper + " already exists !"));
					else
						return (T) directSuper;
				else
					cache.rollback(new FunctionalConsistencyViolationException("Found generic has not correct value : " + homeTreeNode + directSuper.info() + " " + Arrays.toString(supers)));
		}
		return null;
	}

	protected Generic[] getExtendedDirectSupers(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos) {
		return new HashCache<Generic>() {
			private static final long serialVersionUID = 5910353456286109539L;

			@Override
			public Iterator<Generic> cacheSupplier() {
				return new AbstractSelectableLeafIterator(cache.getEngine()) {

					@Override
					public boolean isSelected(Generic candidate) {
						boolean result = ((GenericImpl) candidate).isSuperOf3(homeTreeNode, supers, components);
						if (result)
							return true;
						if (basePos != Statics.MULTIDIRECTIONAL)
							if (((GenericImpl) meta).isSuperOf3(((GenericImpl) candidate).homeTreeNode, ((GenericImpl) candidate).supers, ((GenericImpl) candidate).components)) {
								if (meta.getMetaLevel() != candidate.getMetaLevel()) {
									if (basePos < ((GenericImpl) candidate).components.length)
										if (!components[basePos].equals(((GenericImpl) candidate).components[basePos])) {
											if (components[basePos].inheritsFrom(((GenericImpl) candidate).components[basePos])) {
												if (!candidate.inheritsFrom(cache.find(NoInheritanceSystemType.class)))
													if (isSingular || isProperty && (Arrays.equals(Statics.truncate(basePos, ((GenericImpl) candidate).components), Statics.truncate(basePos, components))))
														return true;

											}
										} else {
											if (((GenericImpl) candidate).equiv(homeTreeNode, new Supers(supers, ((GenericImpl) candidate).supers).toArray(), new Components(components, ((GenericImpl) candidate).components).toArray()))
												return true;
										}
								}
							}
						return false;
					}
				};
			}

			public Generic[] toSortedArray() {
				Generic[] array = toArray(new Generic[size()]);
				Arrays.sort(array);
				return array;
			}
		}.toSortedArray();
	}

	Set<Generic> getDirectDependencies(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos) {
		return new HashCache<Generic>() {

			private static final long serialVersionUID = 2372630315599176801L;

			@Override
			public Iterator<Generic> cacheSupplier() {
				return new AbstractFilterIterator<Generic>(new AbstractPreTreeIterator<Generic>(meta) {
					private static final long serialVersionUID = 3038922934693070661L;
					{
						next();
					}

					@Override
					public Iterator<Generic> children(Generic node) {
						return !isAncestorOf((GenericImpl) node) ? ((GenericImpl) node).<Generic> dependenciesIterator() : Collections.<Generic> emptyIterator();
					}
				}) {
					@Override
					public boolean isSelected() {
						return isAncestorOf(((GenericImpl) next)) || isExtention(next, isProperty, isSingular, basePos);
					}
				};
			}
		};
	}

	private boolean isExtention(Generic candidate, boolean isProperty, boolean isSingular, int basePos) {
		if (Statics.MULTIDIRECTIONAL != basePos)
			if (basePos < ((GenericImpl) candidate).components.length)
				if (((GenericImpl) candidate).components[basePos].inheritsFrom(components[basePos]))
					if (isSingular || (isProperty && Arrays.equals(Statics.truncate(basePos, ((GenericImpl) candidate).components), Statics.truncate(basePos, components))))
						return true;
		return false;
	}

	private boolean isAncestorOf(final GenericImpl dependency) {
		if (GenericImpl.isSuperOf(primaries, components, dependency.primaries, dependency.components))
			return true;
		for (Generic component : dependency.components)
			if (!Arrays.equals(dependency.primaries, ((GenericImpl) component).primaries) || !Arrays.equals(dependency.components, ((GenericImpl) component).components))
				if (isAncestorOf((GenericImpl) component))
					return true;
		return false;
	}

	private static abstract class HashCache<T> extends HashSet<T> {
		private static final long serialVersionUID = 7083886154614346197L;
		{
			Iterator<T> iterator = cacheSupplier();
			while (iterator.hasNext())
				add(iterator.next());
		}

		public abstract Iterator<T> cacheSupplier();
	}
}
