package org.genericsystem.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.genericsystem.core.Statics.Supers;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
class GenericBuilder {
	protected static Logger log = LoggerFactory.getLogger(GenericBuilder.class);

	private final CacheImpl cache;
	private HomeTreeNode homeTreeNode;
	// private HomeTreeNode[] primaries;
	private Generic[] components;
	private Generic[] supers;
	private Generic meta;
	boolean isSingular;
	boolean isProperty;
	int basePos;

	GenericBuilder(CacheImpl cache, Generic meta, HomeTreeNode homeTreeNode, Generic[] aliveSupers, Generic[] aliveNullComponents, int basePos) {
		this.cache = cache;
		this.meta = meta;
		this.homeTreeNode = homeTreeNode;
		supers = aliveSupers;
		components = aliveNullComponents;
		this.basePos = basePos;
		isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isSingularConstraintEnabled(basePos);
		isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isPropertyConstraintEnabled();
		supers = getExtendedDirectSupers(meta, isProperty, isSingular, basePos);
	}

	@SuppressWarnings({ "unchecked" })
	<T extends Generic> T find(boolean existsException) throws RollbackException {
		if (supers.length == 1)
			if (((GenericImpl) supers[0]).equiv(homeTreeNode, ((GenericImpl) supers[0]).supers, components))
				if (existsException)
					cache.rollback(new ExistsException(supers[0] + " already exists !"));
				else
					return (T) supers[0];
		return null;
	}

	<T extends Generic> T bindDependency(Class<?> specializationClass, boolean existsException, boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		return buildDependency(specializationClass, automatic);
	}

	private <T extends Generic> T buildDependency(Class<?> specializationClass, boolean automatic) {
		return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(homeTreeNode, ((GenericImpl) meta).specializeInstanceClass(specializationClass), supers, components), automatic);
	}

	<T extends Generic> T internalBind(final Class<?> specializationClass, int basePos, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		return cache.new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(getDirectDependencies(), basePos);
	}

	private boolean isExtentedBy(Generic candidate, boolean isProperty, boolean isSingular, int basePos) {
		if (Statics.MULTIDIRECTIONAL != basePos)
			if (basePos < ((GenericImpl) candidate).components.length)
				if (!components[basePos].equals(((GenericImpl) candidate).components[basePos]))
					if (components[basePos].inheritsFrom(((GenericImpl) candidate).components[basePos]))
						if (!candidate.inheritsFrom(cache.find(NoInheritanceSystemType.class)))
							if (isSingular || isProperty && (Arrays.equals(Statics.truncate(basePos, ((GenericImpl) candidate).components), Statics.truncate(basePos, components))))
								return true;
		return false;
	}

	protected Generic[] getExtendedDirectSupers(final Generic meta, final boolean isProperty, final boolean isSingular, final int basePos) {
		return new HashCache<Generic>() {
			private static final long serialVersionUID = 5910353456286109539L;

			@Override
			public Iterator<Generic> cacheSupplier() {
				return new AbstractSelectableLeafIterator(cache.getEngine()) {

					@Override
					public boolean isSelected(Generic candidate) {
						boolean result = ((GenericImpl) candidate).isSuperOf_(homeTreeNode, supers, components);
						// if ("Power".equals(candidate.getValue()))
						// log.info("XXXXXXXXX" + candidate.info() + " " + homeTreeNode + " " + Arrays.toString(supers) + " " + Arrays.toString(components) + " " + result);

						if (result)
							return true;
						if (basePos != Statics.MULTIDIRECTIONAL)
							if (((GenericImpl) meta).isSuperOf_(((GenericImpl) candidate).homeTreeNode, ((GenericImpl) candidate).supers, ((GenericImpl) candidate).components))
								if (meta.getMetaLevel() != candidate.getMetaLevel()) {
									if (((GenericImpl) candidate).equiv(homeTreeNode, new Supers(supers, ((GenericImpl) candidate).supers).toArray(), components/* new Components(components, ((GenericImpl) candidate).components).toArray() */))
										return true;
									if (isExtentedBy(candidate, isProperty, isSingular, basePos))
										return true;
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

	Set<Generic> getDirectDependencies() {
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
						boolean result = isAncestorOf(((GenericImpl) next)) || isExtention(next, isProperty, isSingular, basePos);
						// if ("Power".equals(next.getValue()))
						// log.info("TTTTTTTTTT" + next + " " + homeTreeNode + " " + Arrays.toString(supers) + " " + Arrays.toString(components) + " " + result);
						return result;
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
		// boolean result = (GenericImpl.isSuperOf(primaries, components, dependency.primaries, dependency.components));
		boolean result = (GenericImpl.isSuperOf_(homeTreeNode, supers, components, dependency));
		// assert result == result2 : "isSuperOf : " + result + " isSuperOf3 : " + result2 + " homeTreeNode : " + homeTreeNode + " " + supers[0].info() + dependency.info();
		// if ("Power".equals(dependency.getValue()))
		// log.info("UUUUUUUUUU" + dependency + " " + homeTreeNode + " " + Arrays.toString(supers) + " " + Arrays.toString(components) + " " + result);

		if (result)
			return true;
		for (Generic component : dependency.components)
			// if (!Arrays.equals(dependency.primaries, ((GenericImpl) component).primaries) || !Arrays.equals(dependency.components, ((GenericImpl) component).components))
			if (!dependency.equals(component))
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