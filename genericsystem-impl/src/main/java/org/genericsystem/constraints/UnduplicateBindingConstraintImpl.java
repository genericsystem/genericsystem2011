package org.genericsystem.constraints;

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
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(UnduplicateBindingConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UnduplicateBindingConstraintImpl.class)
public class UnduplicateBindingConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(UnduplicateBindingConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(final Generic modified, Generic type,int axe) throws ConstraintViolationException {
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
	@Override
	public void checkConsistency(Generic modified,Holder valueConstraint, int axe) throws ConstraintViolationException {
		// TODO Auto-generated method stub
		
	}
}
