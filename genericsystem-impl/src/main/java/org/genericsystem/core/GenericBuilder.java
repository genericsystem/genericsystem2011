package org.genericsystem.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.genericsystem.core.Statics.OrderedSupers;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;

/**
 * @author Nicolas Feybesse
 * 
 */
class GenericBuilder {
	// protected static Logger log = LoggerFactory.getLogger(GenericBuilder.class);

	private final CacheImpl cache;
	private HomeTreeNode homeTreeNode;
	private UnsafeComponents components;
	private Supers supers;
	private Generic meta;
	private boolean isSingular;
	private boolean isProperty;
	private int basePos;

	GenericBuilder(CacheImpl cache, Generic meta, HomeTreeNode homeTreeNode, Supers aliveSupers, UnsafeComponents components, int basePos, boolean respectSupers) {
		this.cache = cache;
		this.meta = meta;
		this.homeTreeNode = homeTreeNode;
		this.components = components;
		this.basePos = basePos;
		isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isSingularConstraintEnabled(basePos);
		isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) meta).isPropertyConstraintEnabled();
		supers = new OrderedSupers(aliveSupers).toSupers();
		supers = getExtendedDirectSupers(respectSupers);
	}

	protected Supers getExtendedDirectSupers(final boolean respectSupers) {
		final Engine engine = cache.getEngine();
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(engine) {
			{
				if (respectSupers && !supers.get(0).equals(engine))
					iterators.put(engine, new SelectableIterator<>(supers.iterator()));
			}

			@Override
			public boolean isSelected(Generic candidate) {
				if (((GenericImpl) candidate).isSuperOf(homeTreeNode, supers, components))
					return true;
				if (isExtentedBy(candidate)) {
					supers = new OrderedSupers(supers, candidate).toSupers();
					return true;
				}
				return false;
			}
		};
		Set<Generic> set = new TreeSet<>();
		while (iterator.hasNext())
			set.add(iterator.next());
		return new Supers(set);
	}

	boolean containsSuperInMultipleInheritanceValue(Generic candidate) {
		if (supers.size() <= 1 || !containsSuper(candidate))
			return false;
		// log.info("" + candidate + " " + sameHomeTreeNode());
		return (sameHomeTreeNode());
	}

	boolean containsSuper(Generic candidate) {
		for (Generic superGenenic : supers)
			if (candidate.equals(superGenenic))
				return true;
		return false;
	}

	boolean sameHomeTreeNode() {
		for (Generic superGenenic : supers)
			if (!homeTreeNode.equals(((GenericImpl) superGenenic).getHomeTreeNode()))
				return false;
		return true;
	}

	@SuppressWarnings({ "unchecked" })
	<T extends Generic> T find(boolean existsException) throws RollbackException {
		if (supers.size() == 1)
			if (((GenericImpl) supers.get(0)).equiv(homeTreeNode, components))
				if (existsException)
					cache.rollback(new ExistsException(supers.get(0) + " already exists !"));
				else
					return (T) supers.get(0);
		return null;
	}

	<T extends Generic> T bindDependency(Class<?> specializationClass, boolean existsException, boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		return buildDependency(specializationClass, automatic);
	}

	private <T extends Generic> T buildDependency(Class<?> specializationClass, boolean automatic) {
		return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(((GenericImpl) meta).specializeInstanceClass(specializationClass), new UnsafeVertex(homeTreeNode, supers, components)), automatic);
	}

	<T extends Generic> T internalBind(final Class<?> specializationClass, int basePos, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		Generic old = null;
		Set<Generic> directDependencies = getDirectDependencies();
		for (Generic dependency : directDependencies)
			if (!existsException && Statics.MULTIDIRECTIONAL != basePos && (((GenericImpl) dependency).getComponent(basePos)).equals(components.get(basePos))) {
				assert old == null;
				old = dependency;
			}
		for (Generic dependency : directDependencies) {
			assert !supers.contains(dependency) : supers;
			assert !components.contains(dependency) : components;
		}
		return cache.new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(old, directDependencies, basePos);
	}

	Set<Generic> getDirectDependencies() {
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(new AbstractPreTreeIterator<Generic>(meta) {
			private static final long serialVersionUID = 3038922934693070661L;
			{
				next();
			}

			@Override
			public Iterator<Generic> children(Generic node) {
				return !isAncestorOf(node) ? ((GenericImpl) node).<Generic> dependenciesIterator() : Collections.<Generic> emptyIterator();
			}
		}) {
			@Override
			public boolean isSelected() {
				return isAncestorOf((next)) || isExtention(next);
			}
		};

		Set<Generic> set = new TreeSet<>();
		while (iterator.hasNext())
			set.add(iterator.next());
		return set;
	}

	private boolean isExtention(Generic candidate) {
		if (candidate.getMeta().equals(meta)) {
			if (Statics.MULTIDIRECTIONAL != basePos && basePos < ((GenericImpl) candidate).components().size()) {
				if (isSingular && ((GenericImpl) candidate).getComponent(basePos).inheritsFrom(components.get(basePos)))
					return true;
				if (isProperty && areComponentsInheriting((((GenericImpl) candidate).components()), components))
					return true;
			}
		}
		return false;
	}

	private static boolean areComponentsInheriting(List<Generic> subComponents, List<Generic> components) {
		for (int i = 0; i < components.size(); i++)
			if (!subComponents.get(i).inheritsFrom(components.get(i)))
				return false;
		return true;
	}

	private boolean isExtentedBy(Generic candidate) {
		if (Statics.MULTIDIRECTIONAL != basePos)
			if (basePos < ((GenericImpl) candidate).components().size())
				if (!components.get(basePos).equals(((GenericImpl) candidate).getComponent(basePos)))
					if ((((Attribute) meta).isInheritanceEnabled()))
						if (homeTreeNode.getMetaLevel() == candidate.getMetaLevel()) {
							if (isSingular && components.get(basePos).inheritsFrom(((GenericImpl) candidate).getComponent(basePos)))
								return true;
							if (isProperty && areComponentsInheriting(components, ((GenericImpl) candidate).components()))
								return true;
						}
		return false;
	}

	private boolean isAncestorOf(final Generic dependency) {
		if (((GenericImpl) dependency).inheritsFrom(homeTreeNode, supers, components))
			return true;
		for (Generic component : ((GenericImpl) dependency).components())
			if (!dependency.equals(component))
				if (isAncestorOf(component))
					return true;
		return false;
	}
}
