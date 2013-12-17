package org.genericsystem.constraints;

import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.HomeTreeNode;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ UnduplicateBindingConstraintImpl.DefaultKey.class, UnduplicateBindingConstraintImpl.DefaultValue.class })
@Priority(-1)
public class UnduplicateBindingConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = UnduplicateBindingConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(UnduplicateBindingConstraintImpl.class)
	public static class DefaultKey extends UnduplicateBindingConstraintImpl {
	}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic instanceToCheck, final Generic baseConstraint) throws ConstraintViolationException {
		final Generic[] supers = ((GenericImpl) baseConstraint).getSupersArray();
		final Generic[] components = ((GenericImpl) baseConstraint).getComponentsArray();

		final HomeTreeNode homeTreeNode = ((GenericImpl) baseConstraint).getHomeTreeNode();

		Iterator<Generic> iterator = components.length == 0 || components[0] == null ? supers[0].getInheritings().iterator() : components[0].getComposites().iterator();
		iterator = new AbstractFilterIterator<Generic>(iterator) {

			@Override
			public boolean isSelected() {
				return !next.equals(baseConstraint) && ((GenericImpl) next).equiv(homeTreeNode, supers, components);
			}
		};

		if (iterator.hasNext())
			throw new UnduplicateBindingConstraintViolationException();

		// Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(components.length > 0 && components[0] != null ? ((EngineImpl) baseConstraint.getEngine()).getCurrentCache().compositesIterator(components[0])
		// : ((AbstractContext) ((EngineImpl) baseConstraint.getEngine()).getCurrentCache()).directInheritingsIterator(supers[0])) {
		// @Override
		// public boolean isSelected() {
		// return Arrays.equals(((GenericImpl) next).getSupersArray(), supers) && Arrays.equals(((GenericImpl) next).getComponentsArray(), components) && Objects.equals(baseConstraint.getValue(), next.getValue());
		// }
		// };
		// if (iterator.hasNext()) {
		// iterator.next();
		// if (iterator.hasNext())
		// throw new UnduplicateBindingConstraintViolationException();
		// }
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return true;
	}
}
