package org.genericsystem.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.core.Statics.OrderedDependencies;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
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

	private UnsafeVertex uVertex;
	private Boolean isStrongSingular[];
	private Boolean isProperty;// TODO KK change for strongProperty ?

	GenericBuilder(UnsafeVertex uVertex, boolean respectSupers) {
		int dim = uVertex.components().size();
		this.uVertex = uVertex;
		isStrongSingular = new Boolean[dim];
		this.uVertex = new UnsafeVertex(uVertex.homeTreeNode(), uVertex.getMeta(), getExtendedDirectSupers(respectSupers), uVertex.components());
	}

	private boolean isStrongSingular(int i) {
		if (isStrongSingular[i] == null)
			isStrongSingular[i] = ((GenericImpl) uVertex.getMeta()).isSingularConstraintEnabled(i) && !((GenericImpl) uVertex.getMeta()).isReferentialIntegrity(i);
		return isStrongSingular[i];
	}

	private boolean isProperty() {
		return isProperty != null ? isProperty : (isProperty = ((GenericImpl) uVertex.getMeta()).isPropertyConstraintEnabled());
	}

	protected Supers getExtendedDirectSupers(final boolean respectSupers) {
		final Engine engine = ((GenericImpl) uVertex.getMeta()).getEngine();
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(engine) {
			{
				if (respectSupers && !uVertex.supers().iterator().next().equals(engine))
					iterators.put(engine, new SelectableIterator<>(uVertex.supers().iterator()));
			}

			@Override
			public boolean isSelected(Generic candidate) {
				// log.info("zzzzzzzzzz" + ((GenericImpl) candidate).isSuperOf(uVertex) + " " + candidate.info() + " " + uVertex.homeTreeNode() + " " + uVertex.components() + " " + uVertex.supers());
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
		return ((GenericImpl) uVertex.getMeta()).getCurrentCache();
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
		// TODO impl
		// new Metas<T>(homeTreeNode().metaNode).getMeta(this)
		// return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(((GenericImpl) new Metas<T>(uVertex.homeTreeNode().metaNode).getMeta(this)).specializeInstanceClass(specializationClass), uVertex), automatic);
		return cache.<T> insert(cache.<EngineImpl> getEngine().buildComplex(((GenericImpl) uVertex.getMeta()).specializeInstanceClass(specializationClass), uVertex), automatic);
	}

	// private class Metas<T extends Generic> extends HashSet<Generic> {
	//
	// private static final long serialVersionUID = 783352418448187992L;
	//
	// private final HomeTreeNode metaNode;
	//
	// public Metas(HomeTreeNode metaNode) {
	// this.metaNode = metaNode;
	// }
	//
	// @Override
	// public boolean add(Generic candidate) {
	// for (Generic generic : this)
	// if (generic.inheritsFrom(candidate))
	// return false;
	// Iterator<Generic> it = iterator();
	// while (it.hasNext())
	// if (candidate.inheritsFrom(it.next()))
	// it.remove();
	// return super.add(candidate);
	// }
	//
	// public T getMeta(Generic generic) {
	// if (generic.isEngine())
	// add(generic);
	// else {
	// for (Generic superGeneric : ((GenericImpl) generic).getSupers())
	// if (((GenericImpl) superGeneric).homeTreeNode().equals(metaNode))
	// add(superGeneric);
	// for (Generic superGeneric : ((GenericImpl) generic).getSupers())
	// if (((GenericImpl) superGeneric).homeTreeNode().inheritsFrom(metaNode) && !((GenericImpl) superGeneric).homeTreeNode().equals(metaNode))
	// add(getMeta(superGeneric));
	// }
	// return (T) unambigousFirst(iterator());
	// }
	//
	// }

	<T extends Generic> T internalBind(final Class<?> specializationClass, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;

		Generic old = null;
		NavigableSet<Generic> directDependencies = getDependencies();
		for (Generic dependency : directDependencies)
			if (!existsException)
				if (Statics.CONCRETE == uVertex.metaLevel())
					for (int pos = 0; pos < uVertex.components().size(); pos++)
						// TODO KK do not work for properties when dim > 1 !
						if ((isProperty() || isStrongSingular(pos)) && (((GenericImpl) dependency).getComponent(pos)).equals(uVertex.components().get(pos))) {
							assert old == null || old == dependency;
							old = dependency;
						}
		for (Generic dependency : directDependencies) {
			assert !uVertex.supers().contains(dependency) : uVertex.supers().get(0).info() + " " + uVertex.components();
			assert !uVertex.components().contains(dependency) : uVertex.components();
			assert !((GenericImpl) dependency).equiv(uVertex) : dependency.info() + ((GenericImpl) dependency).isSuperOf(uVertex);
		}
		return getCurrentCache().new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(old, directDependencies);
	}

	NavigableSet<Generic> getDependencies() {
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(new AbstractPreTreeIterator<Generic>((uVertex.getMeta())) {
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

		OrderedDependencies dependencies = new OrderedDependencies();
		while (iterator.hasNext())
			dependencies.addDependencies(iterator.next());
		return dependencies;
	}

	private boolean isExtention(Generic candidate) {
		if (Statics.CONCRETE == uVertex.metaLevel() && candidate.getMeta().equals((uVertex.getMeta()))) {
			if (isProperty() && areComponentsInheriting((((GenericImpl) candidate).getComponents()), uVertex.components()))
				return true;
			for (int pos = 0; pos < ((GenericImpl) candidate).getComponents().size(); pos++)
				if (isStrongSingular(pos))
					if (((GenericImpl) candidate).getComponent(pos).inheritsFrom(uVertex.components().get(pos))) {
						if (!((GenericImpl) candidate).getComponent(pos).equals(uVertex.components().get(pos)))
							return true;
						if (uVertex.components().equals(((GenericImpl) candidate).getComponents()))
							return true;
						if (!areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
							return true;
					}
		}
		return false;

	}

	private boolean isExtentedBy(Generic candidate) {
		if (Statics.CONCRETE == uVertex.metaLevel() && ((GenericImpl) uVertex.getMeta()).isInheritanceEnabled() && candidate.getMeta().equals((uVertex.getMeta()))) {
			if (((GenericImpl) candidate).homeTreeNode().equals(uVertex.homeTreeNode()) || !uVertex.components().equals(((GenericImpl) candidate).getComponents()))
				if (isProperty() && areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
					return true;
			for (int pos = 0; pos < uVertex.components().size(); pos++) {
				if ((isStrongSingular(pos))) {
					if (((GenericImpl) candidate).homeTreeNode().equals(uVertex.homeTreeNode()) || (!uVertex.components().get(pos).equals(((GenericImpl) candidate).getComponent(pos))))
						if (uVertex.components().get(pos).inheritsFrom(((GenericImpl) candidate).getComponent(pos))) {
							if (!uVertex.components().get(pos).equals(((GenericImpl) candidate).getComponent(pos)))
								return true;
							if (areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
								return true;
						}
				}
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
