package org.genericsystem.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import org.genericsystem.core.Statics.Primaries;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.FunctionalConsistencyViolationException;
import org.genericsystem.exception.RollbackException;
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

	Vertex(CacheImpl cache, HomeTreeNode homeTreeNode, HomeTreeNode[] primaries, Generic[] components) {
		this.cache = cache;
		this.homeTreeNode = homeTreeNode;
		this.primaries = primaries;
		this.components = components;
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

	public <T extends Generic> T findAndMute(Generic meta, boolean isProperty, boolean isSingular, int basePos, boolean existsException) {
		computeSupersAndMute(meta, isProperty, isSingular, basePos);
		return findInSupers(existsException);
	}

	void computeSupersAndMute(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos) {
		supers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos, primaries, components);
		for (Generic directSuper : supers) {
			primaries = new Primaries(directSuper, primaries).toArray();
			components = GenericImpl.enrich(components, ((GenericImpl) directSuper).components);
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
					cache.rollback(new FunctionalConsistencyViolationException(directSuper.info() + " " + supers));
		}
		return null;
	}

	protected Generic[] getExtendedDirectSupers(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos, final HomeTreeNode[] primaries, final Generic[] components) {
		return new TreeSet<Generic>() {
			private static final long serialVersionUID = 8568383988023387246L;
			{
				Iterator<Generic> iterator = extendedDirectSupersIterator(meta, isProperty, isSingular, basePos, primaries, components);
				while (iterator.hasNext())
					add(iterator.next());
			}

			Iterator<Generic> extendedDirectSupersIterator(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos, final HomeTreeNode[] primaries, final Generic[] components) {
				return new AbstractSelectableLeafIterator(cache.getEngine()) {

					@Override
					public boolean isSelected(Generic candidate) {
						boolean result = GenericImpl.isSuperOf(((GenericImpl) candidate).primaries, ((GenericImpl) candidate).components, primaries, components);
						if (result)
							return true;
						if (basePos != Statics.MULTIDIRECTIONAL)
							if (GenericImpl.isSuperOf(((GenericImpl) meta).primaries, ((GenericImpl) meta).components, ((GenericImpl) candidate).primaries, ((GenericImpl) candidate).components)) {
								if (meta.getMetaLevel() != candidate.getMetaLevel()) {
									if (basePos < ((GenericImpl) candidate).components.length && !components[basePos].equals(((GenericImpl) candidate).components[basePos])) {
										if (components[basePos].inheritsFrom(((GenericImpl) candidate).components[basePos])) {
											if (!candidate.inheritsFrom(cache.find(NoInheritanceSystemType.class)))
												if (isSingular || isProperty && (Arrays.equals(Statics.truncate(basePos, ((GenericImpl) candidate).components), Statics.truncate(basePos, components))))
													return true;

										}
									} else {
										if (((GenericImpl) candidate).equiv(new Primaries(candidate, primaries).toArray(), GenericImpl.enrich(components, ((GenericImpl) candidate).components)))
											return true;
									}
								}
							}
						return false;
					}
				};
			}

			@Override
			public Generic[] toArray() {
				return toArray(new Generic[size()]);
			};
		}.toArray();
	}

}
