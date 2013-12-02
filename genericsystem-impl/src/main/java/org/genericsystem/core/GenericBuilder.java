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
import org.genericsystem.iterator.ArrayIterator;
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

	GenericBuilder(CacheImpl cache, Generic meta, HomeTreeNode homeTreeNode, Generic[] aliveSupers, Generic[] aliveNullComponents, int basePos, boolean respectSupers) {
		this.cache = cache;
		this.meta = meta;
		this.homeTreeNode = homeTreeNode;
		components = aliveNullComponents;
		this.basePos = basePos;
		isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isSingularConstraintEnabled(basePos);
		isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isPropertyConstraintEnabled();
		supers = new Supers(aliveSupers).toArray();
		supers = getExtendedDirectSupers(respectSupers);
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
		Generic old = null;
		Set<Generic> directDependencies = getDirectDependencies();
		for (Generic dependency : directDependencies)
			if (Statics.MULTIDIRECTIONAL != basePos && (((GenericImpl) dependency).getComponent(basePos)).equals(components[basePos])) {
				assert old == null;
				old = dependency;
			}
		for (Generic dependency : directDependencies) {
			assert !Arrays.asList(supers).contains(dependency) : Arrays.toString(supers);
			assert !Arrays.asList(components).contains(dependency) : Arrays.toString(components);
		}
		// log.info("Supers : " + Arrays.toString(supers));
		return cache.new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(old, getDirectDependencies(), basePos);
	}

	private boolean isExtentedBy(Generic candidate) {

		if (Statics.MULTIDIRECTIONAL != basePos)
			if (basePos < ((GenericImpl) candidate).components.length)
				if (!components[basePos].equals(((GenericImpl) candidate).components[basePos]))
					if (!candidate.inheritsFrom(cache.find(NoInheritanceSystemType.class))) {
						if (homeTreeNode.getMetaLevel() == candidate.getMetaLevel()) {
							if (isSingular && components[basePos].inheritsFrom(((GenericImpl) candidate).components[basePos]))
								return true;
							if (isProperty && areComponentsInheriting(components, ((GenericImpl) candidate).components))
								return true;
						}
					}
		return false;
	}

	private boolean areComponentsInheriting(Generic[] subComponents, Generic[] components) {
		for (int i = 0; i < components.length; i++)
			if (!subComponents[i].inheritsFrom(components[i]))
				return false;
		return true;
	}

	protected Generic[] getExtendedDirectSupers(final boolean respectSupers) {
		return new HashCache<Generic>() {
			private static final long serialVersionUID = 5910353456286109539L;

			@Override
			public Iterator<Generic> cacheSupplier() {
				final Engine engine = cache.getEngine();
				return new AbstractSelectableLeafIterator(engine) {
					{
						if (respectSupers && !supers[0].equals(engine))
							iterators.put(engine, new SelectableIterator<>(new ArrayIterator<>(supers)));
					}

					@Override
					public boolean isSelected(Generic candidate) {
						boolean result = ((GenericImpl) candidate).isSuperOf(homeTreeNode, supers, components);
						if (result)
							return true;
						if (basePos != Statics.MULTIDIRECTIONAL) {
							if (((GenericImpl) candidate).equiv(homeTreeNode, new Supers(supers, ((GenericImpl) candidate).supers).toArray(), components))
								return true;
							if (isExtentedBy(candidate))
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
				if (homeTreeNode.getMetaLevel() == candidate.getMetaLevel()) {
					if (isSingular && ((GenericImpl) candidate).components[basePos].inheritsFrom(components[basePos]))
						return true;
					if (isProperty && areComponentsInheriting((((GenericImpl) candidate).components), components))
						return true;
				}
		return false;
	}

	private boolean isAncestorOf(final GenericImpl dependency) {
		// boolean result = (GenericImpl.isSuperOf(primaries, components, dependency.primaries, dependency.components));
		boolean result = (GenericImpl.isSuperOf(homeTreeNode, supers, components, dependency));
		// assert result == result2 : "isSuperOf : " + result + " isSuperOf3 : " + result2 + " homeTreeNode : " + homeTreeNode + " " + supers[0].info() + dependency.info();
		// if ("Power".equals(dependency.getValue()))
		// log.info("UUUUUUUUUU" + dependency + " " + homeTreeNode + " " + Arrays.toString(supers) + " " + Arrays.toString(components) + " " + result);

		if (result)
			return true;
		for (Generic component : dependency.components)
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
