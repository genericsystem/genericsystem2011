package org.genericsystem.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import org.genericsystem.core.Statics.OrderedDependencies;
import org.genericsystem.exception.ExistsException;
import org.genericsystem.exception.RollbackException;
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

	GenericBuilder(UnsafeVertex uVertex, boolean respectSupers) {
		super(uVertex, respectSupers);
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

	<T extends Generic> T bindDependency(Class<?> specializationClass, boolean existsException, boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;
		return buildDependency(specializationClass, automatic);
	}

	private <T extends Generic> T buildDependency(Class<?> specializationClass, boolean automatic) {
		CacheImpl cache = getCurrentCache();
		return cache.<T> insert(cache.<EngineImpl> getEngine().build(((GenericImpl) getMeta()).specializeInstanceClass(specializationClass), this), automatic);
	}

	<T extends Generic> T internalBind(final Class<?> specializationClass, boolean existsException, final boolean automatic) throws RollbackException {
		T result = find(existsException);
		if (result != null)
			return result;

		Generic[] toReplace = new Generic[1];
		NavigableSet<Generic> dependencies = getDependencies(toReplace, existsException);
		for (Generic dependency : dependencies) {
			assert !supers().contains(dependency) : dependency.info() + supers().get(0).info() + " " + components();
			assert !components().contains(dependency) : components();
			assert !((GenericImpl) dependency).equiv(this) : dependency.info() + ((GenericImpl) dependency).isSuperOf(this);
		}
		return getCurrentCache().new Restructurator() {
			private static final long serialVersionUID = 1370210509322258062L;

			@Override
			Generic rebuild() {
				return GenericBuilder.this.buildDependency(specializationClass, automatic);
			}
		}.rebuildAll(toReplace[0], dependencies);
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
					if (((GenericImpl) candidate).getComponent(pos).equals(components().get(pos))) {
						if (!((GenericImpl) candidate).isSuperOf(this))
							return true;
						// if (!(((GenericImpl) candidate).homeTreeNode().equals(uVertex.homeTreeNode())) || !areComponentsInheriting(uVertex.components(), ((GenericImpl) candidate).getComponents()))
						// return true;
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
		if (((GenericImpl) dependency).inheritsFrom(this))
			return true;
		for (Generic component : ((GenericImpl) dependency).getComponents())
			if (!dependency.equals(component))
				if (isAncestorOf(component))
					return true;
		return false;
	}
}
