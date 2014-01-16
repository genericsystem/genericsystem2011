package org.genericsystem.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
class GenericBuilder {
	protected static Logger log = LoggerFactory.getLogger(GenericBuilder.class);

	private final CacheImpl cache;
	private UnsafeVertex uVertex;
	private int basePos;
	private boolean isSingular;
	private boolean isProperty;

	GenericBuilder(CacheImpl cache, UnsafeVertex uVertex, int basePos, boolean respectSupers) {
		this.cache = cache;
		this.uVertex = uVertex;
		this.basePos = basePos;
		isSingular = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) uVertex.getMeta()).isSingularConstraintEnabled(basePos);
		isProperty = Statics.MULTIDIRECTIONAL != basePos && ((GenericImpl) uVertex.getMeta()).isPropertyConstraintEnabled();
		this.uVertex = new UnsafeVertex(uVertex.homeTreeNode(), uVertex.getMeta(), getExtendedDirectSupers(respectSupers), uVertex.components());
	}

	protected Supers getExtendedDirectSupers(final boolean respectSupers) {
		final Engine engine = cache.getEngine();
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(engine) {
			{
				if (respectSupers && !uVertex.supers().iterator().next().equals(engine))
					iterators.put(engine, new SelectableIterator<>(uVertex.supers().iterator()));
			}

			@Override
			public boolean isSelected(Generic candidate) {
				return ((GenericImpl) candidate).isSuperOf(uVertex) || isExtentedBy(candidate);
			}
		};
		Set<Generic> set = new TreeSet<>();
		while (iterator.hasNext())
			set.add(iterator.next());
		// if (respectSupers) {
		// LOOP: for (Generic generic : uVertex.supers()) {
		// for (Generic newSuper : set)
		// if (newSuper.inheritsFrom(generic))
		// continue LOOP;
		// throw new IllegalStateException("Invalid super : " + generic);
		// }
		// }

		return new Supers(set);
	}

	boolean containsSuperInMultipleInheritanceValue(Generic candidate) {
		if (uVertex.supers().size() <= 1 || !containsSuper(candidate))
			return false;
		return (sameHomeTreeNode());
	}

	boolean containsSuper(Generic candidate) {
		for (Generic superGenenic : uVertex.supers())
			if (candidate.equals(superGenenic))
				return true;
		return false;
	}

	boolean sameHomeTreeNode() {
		for (Generic superGenenic : uVertex.supers())
			if (!uVertex.homeTreeNode().equals(((GenericImpl) superGenenic).homeTreeNode()))
				return false;
		return true;
	}

	@SuppressWarnings({ "unchecked" })
	<T extends Generic> T find(boolean existsException) throws RollbackException {
		if (uVertex.supers().size() == 1)
			if (((GenericImpl) uVertex.supers().get(0)).equiv(uVertex.homeTreeNode(), uVertex.components()))
				if (existsException)
					cache.rollback(new ExistsException(uVertex.supers().get(0) + " already exists !"));
				else
					return (T) uVertex.supers().get(0);
		return null;
	}

	<T extends Generic> T bindDependency(Class<?> specializationClass, boolean existsException, boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		return buildDependency(specializationClass, automatic);
	}

	private <T extends Generic> T buildDependency(Class<?> specializationClass, boolean automatic) {
		return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(((GenericImpl) uVertex.getMeta()).specializeInstanceClass(specializationClass), uVertex), automatic);
	}

	<T extends Generic> T internalBind(final Class<?> specializationClass, int basePos, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;

		Generic old = null;
		Set<Generic> directDependencies = getDirectDependencies();
		for (Generic dependency : directDependencies)
			if (!existsException && Statics.MULTIDIRECTIONAL != basePos && (((GenericImpl) dependency).getComponent(basePos)).equals(uVertex.components().get(basePos))) {
				assert old == null;
				old = dependency;
			}
		for (Generic dependency : directDependencies) {
			assert !uVertex.supers().contains(dependency) : uVertex.supers();
			assert !uVertex.components().contains(dependency) : uVertex.components();
			assert !((GenericImpl) dependency).equiv(uVertex);
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
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(new AbstractPreTreeIterator<Generic>(uVertex.getMeta()) {
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
		if (candidate.getMeta().equals(uVertex.getMeta())) {
			if (Statics.MULTIDIRECTIONAL != basePos && basePos < ((GenericImpl) candidate).getComponents().size()) {
				if (isSingular && ((GenericImpl) candidate).getComponent(basePos).inheritsFrom(uVertex.components().get(basePos)))
					return true;
				if ((isProperty) && areComponentsInheriting((((GenericImpl) candidate).getComponents()), uVertex.components()))
					return true;
			}
		}
		return false;
	}

	private boolean isExtentedBy(Generic candidate) {
		if (Statics.MULTIDIRECTIONAL != basePos && basePos < ((GenericImpl) candidate).getComponents().size())
			if (((GenericImpl) candidate).homeTreeNode().equals(uVertex.homeTreeNode()) || !uVertex.components().get(basePos).equals(((GenericImpl) candidate).getComponent(basePos)))
				if ((((Attribute) uVertex.getMeta()).isInheritanceEnabled()))
					if (uVertex.homeTreeNode().getMetaLevel() == candidate.getMetaLevel()) {
						if (isSingular && uVertex.components().get(basePos).inheritsFrom(((GenericImpl) candidate).getComponent(basePos)) && (!uVertex.components().get(basePos).equals(((GenericImpl) candidate).getComponent(basePos))))
							return true;
						if ((isSingular || isProperty) && areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
							return true;
					}
		return false;
	}

	private static boolean areComponentsInheriting(List<Generic> subComponents, List<Generic> components) {
		for (int i = 0; i < components.size(); i++)
			if (!subComponents.get(i).inheritsFrom(components.get(i)))
				return false;
		return true;
	}

	private boolean isAncestorOf(final Generic dependency) {
		if (((GenericImpl) dependency).inheritsFrom(uVertex))
			return true;
		for (Generic component : ((GenericImpl) dependency).getComponents())
			if (!dependency.equals(component))
				if (isAncestorOf(component))
					return true;
		return false;
	}
}
