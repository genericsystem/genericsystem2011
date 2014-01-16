package org.genericsystem.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
class GenericBuilder2 {
	protected static Logger log = LoggerFactory.getLogger(GenericBuilder.class);

	private UnsafeVertex uVertex;
	private Generic meta;
	private boolean isSingular[];
	private boolean isReferentialIntegrity[];
	private boolean isProperty;

	GenericBuilder2(Generic meta, UnsafeVertex uVertex, boolean respectSupers) {
		int dim = uVertex.components().size();
		this.meta = meta;
		this.uVertex = uVertex;
		isSingular = new boolean[dim];
		isReferentialIntegrity = new boolean[dim];
		for (int i = 0; i < dim; i++) {
			isSingular[i] = ((GenericImpl) meta).isSingularConstraintEnabled(i);
			isReferentialIntegrity[i] = ((GenericImpl) meta).isReferentialIntegrity(i);
		}
		isProperty = ((GenericImpl) meta).isPropertyConstraintEnabled();
		this.uVertex = new UnsafeVertex(uVertex.homeTreeNode(), meta, getExtendedDirectSupers(respectSupers), uVertex.components());
	}

	protected Supers getExtendedDirectSupers(final boolean respectSupers) {
		final Engine engine = meta.getEngine();
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

	private CacheImpl getCurrentCache() {
		return ((GenericImpl) meta).getCurrentCache();
	}

	@SuppressWarnings({ "unchecked" })
	<T extends Generic> T find(boolean existsException) throws RollbackException {
		if (uVertex.supers().size() == 1)
			if (((GenericImpl) uVertex.supers().get(0)).equiv(uVertex.homeTreeNode(), uVertex.components()))
				if (existsException)
					getCurrentCache().rollback(new ExistsException(uVertex.supers().get(0) + " already exists !"));
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
		CacheImpl cache = getCurrentCache();
		return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(((GenericImpl) meta).specializeInstanceClass(specializationClass), uVertex), automatic);
	}

	<T extends Generic> T internalBind(final Class<?> specializationClass, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;

		Generic old = null;
		Set<Generic> directDependencies = getDirectDependencies();
		for (Generic dependency : directDependencies)
			if (!existsException)
				for (int pos = 0; pos < uVertex.components().size(); pos++)
					if (((!isReferentialIntegrity[pos] && isSingular[pos]) || isProperty) && (((GenericImpl) dependency).getComponent(pos)).equals(uVertex.components().get(pos))) {
						assert old == null || old == dependency;
						old = dependency;
					}
		for (Generic dependency : directDependencies) {
			assert !uVertex.supers().contains(dependency) : uVertex.supers().get(0).info() + " " + uVertex.components();
			assert !uVertex.components().contains(dependency) : uVertex.components();
			assert !((GenericImpl) dependency).equiv(uVertex);
		}
		return getCurrentCache().new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder2.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(old, directDependencies, Statics.MULTIDIRECTIONAL);
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
		if (Statics.CONCRETE == uVertex.metaLevel() && candidate.isConcrete())
			if (candidate.getMeta().equals(meta)) {
				for (int pos = 0; pos < ((GenericImpl) candidate).getComponents().size(); pos++) {
					if ((!isReferentialIntegrity[pos] && isSingular[pos]) && ((GenericImpl) candidate).getComponent(pos).inheritsFrom(uVertex.components().get(pos)) && !((GenericImpl) candidate).getComponent(pos).equals(uVertex.components().get(pos)))
						return true;
				}
				if (isProperty && areComponentsInheriting((((GenericImpl) candidate).getComponents()), uVertex.components()))
					return true;
			}
		return false;
	}

	private boolean isExtentedBy(Generic candidate) {
		if (Statics.CONCRETE == uVertex.metaLevel() && candidate.isConcrete())
			for (int pos = 0; pos < uVertex.components().size(); pos++) {
				if (((GenericImpl) candidate).homeTreeNode().equals(uVertex.homeTreeNode()) || !Objects.equals(uVertex.components().get(pos), (((GenericImpl) candidate).getComponent(pos))))
					if ((((Attribute) meta).isInheritanceEnabled()))
						if (uVertex.homeTreeNode().getMetaLevel() == candidate.getMetaLevel()) {
							if ((!isReferentialIntegrity[pos] && isSingular[pos]) && uVertex.components().get(pos).inheritsFrom(((GenericImpl) candidate).getComponent(pos))
									&& (!uVertex.components().get(pos).equals(((GenericImpl) candidate).getComponent(pos))))
								return true;
							if (((!isReferentialIntegrity[pos] && isSingular[pos]) || isProperty) && areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
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
