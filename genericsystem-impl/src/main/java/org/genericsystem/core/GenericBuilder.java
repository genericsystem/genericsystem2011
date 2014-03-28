package org.genericsystem.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;

import org.genericsystem.core.Statics.OrderedDependencies;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
class GenericBuilder extends UnsafeVertex {
	protected static Logger log = LoggerFactory.getLogger(GenericBuilder.class);

	GenericBuilder(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components, boolean respectSupers) {
		super(homeTreeNode, supers, components, respectSupers);
	}

	private CacheImpl getCurrentCache() {
		return ((GenericImpl) getMeta()).getCurrentCache();
	}

	@SuppressWarnings({ "unchecked" })
	<T extends Generic> T find(boolean existsException) throws RollbackException {
		if (supers().size() == 1)
			if (((GenericImpl) supers().get(0)).equiv(homeTreeNode(), components()))
				if (existsException)
					getCurrentCache().rollback(new ExistsException(supers().get(0) + " already exists !"));
				else
					return (T) supers().get(0);
		return null;
	}

	private boolean isSameSignature(Generic compare) {
		// TODO factories with getGeneric
		return ((GenericImpl) compare).homeTreeNode().equals(homeTreeNode()) && getMeta().equals(compare.getMeta()) && Arrays.equals(compare.getComponents().toArray(), components().toArray());
	}

	<T extends Generic> T simpleBind(Class<?> specializationClass, boolean existsException, boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		CacheImpl cache = getCurrentCache();
		return cache.<T> insert(cache.<EngineImpl> getEngine().build(((GenericImpl) getMeta()).specializeInstanceClass(specializationClass), this), automatic);
	}

	<T extends Generic> T bind(final Class<?> specializationClass, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;

		if (supers().size() > 1)
			for (Generic superGeneric : supers())
				if (isSameSignature(superGeneric))
					return ((GenericImpl) superGeneric).addSupers(Statics.truncate(superGeneric, supers()).toArray());

		Generic[] toReplace = new Generic[1];
		NavigableSet<Generic> dependencies = getDependencies(toReplace, existsException);
		for (Generic dependency : dependencies) {
			assert !supers().contains(dependency) : dependency.info() + " / " + supers();
			assert !components().contains(dependency) : dependency.info() + " / " + components();
			assert !((GenericImpl) dependency).equiv(this) : dependency.info() + ((GenericImpl) dependency).isSuperOf(this);
		}
		return projectDefaultValues(getCurrentCache().new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.simpleBind(specializationClass, false, automatic);
			}
		}.<T> rebuildAll(toReplace[0], dependencies));
	}

	private <T extends Generic> T projectDefaultValues(T bind) {
		if (bind.isAttribute())
			for (Generic superGeneric : supers())
				if (superGeneric.isStructural() && superGeneric.getMetaLevel() == metaLevel()) {
					List<Generic> components = superGeneric.getComponents();
					for (int pos = 0; pos < components.size(); pos++)
						for (Generic defaultHolder : components.get(pos).getHolders((Holder) superGeneric, pos))
							internalProject(bind, pos, defaultHolder);
				}
		return bind;
	}

	public void internalProject(Generic bind, final int pos, final Generic defaultHolder) {
		Generic projection = ((GenericImpl) bind).unambigousFirst(new AbstractFilterIterator<Generic>(((GenericImpl) defaultHolder).allInheritingsIteratorWithoutRoot()) {
			@Override
			public boolean isSelected() {
				return ((GenericImpl) next).inheritsFrom(((GenericImpl) next).filterToProjectVertex(new UnsafeComponents(defaultHolder.getComponents()), pos));
			}
		});
		if (projection == null) {
			Generic[] components = Statics.replace(pos, (Generic[]) defaultHolder.getComponents().toArray(), ((GenericImpl) bind).getComponent(pos));
			((GenericImpl) defaultHolder.getMeta()).createNewBuilder(((GenericImpl) defaultHolder).homeTreeNode(), defaultHolder, Statics.EMPTY_GENERIC_ARRAY, components).bind(defaultHolder.getClass(), false, true);
		}
	}

	NavigableSet<Generic> getDependencies(final Generic[] toReplace, final boolean existException) {
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(new AbstractPreTreeIterator<Generic>((getMeta())) {
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
				if (isAncestorOf((next)))
					return true;
				if (!existException && isExtention(next)) {
					toReplace[0] = next;
					return true;
				}
				return false;

			}
		};

		OrderedDependencies dependencies = new OrderedDependencies();
		while (iterator.hasNext())
			dependencies.addDependencies(iterator.next());
		return dependencies;
	}

	private boolean isExtention(Generic candidate) {
		if (Statics.CONCRETE == metaLevel() && candidate.getMeta().equals((getMeta()))) {
			if (((GenericImpl) getMeta()).isPropertyConstraintEnabled())
				if (areComponentsInheriting((((GenericImpl) candidate).getComponents()), components()))
					return true;
			for (int pos = 0; pos < ((GenericImpl) candidate).getComponents().size(); pos++)
				if (((GenericImpl) getMeta()).isSingularConstraintEnabled(pos) && !((GenericImpl) getMeta()).isReferentialIntegrity(pos))
					if (((GenericImpl) candidate).getComponent(pos).equals(components().get(pos)))
						if (!((GenericImpl) candidate).isSuperOf(this))
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
		if (((GenericImpl) dependency).inheritsFrom(this))
			return true;
		for (Generic component : ((GenericImpl) dependency).getComponents())
			if (!dependency.equals(component))
				if (isAncestorOf(component))
					return true;
		return false;
	}
}
