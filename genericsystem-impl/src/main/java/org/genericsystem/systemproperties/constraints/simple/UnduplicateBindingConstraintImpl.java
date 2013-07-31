package org.genericsystem.systemproperties.constraints.simple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(ConstraintKey.class)
@SingularConstraint
@Dependencies(UnduplicateBindingConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UnduplicateBindingConstraintImpl.class)
public class UnduplicateBindingConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(UnduplicateBindingConstraintImpl.class)
	@Extends(ConstraintsMapProvider.ConstraintValue.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(final Generic modified, Generic type) throws ConstraintViolationException {
		final Generic[] supers = ((GenericImpl) modified).getSupersArray();
		final Generic[] components = ((GenericImpl) modified).getComponentsArray();
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(components.length > 0 && components[0] != null ? ((EngineImpl) modified.getEngine()).getCurrentCache().compositesIterator(components[0])
				: ((AbstractContext) ((EngineImpl) modified.getEngine()).getCurrentCache()).directInheritingsIterator(supers[0])) {
			@Override
			public boolean isSelected() {
				return Arrays.equals(((GenericImpl) next).getSupersArray(), supers) && Arrays.equals(((GenericImpl) next).getComponentsArray(), components) && Objects.equals(modified.getValue(), next.getValue());
			}
		};
		if (iterator.hasNext()) {
			iterator.next();
			if (iterator.hasNext())
				throw new UnduplicateBindingConstraintViolationException();
		}
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
