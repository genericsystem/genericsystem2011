package org.genericsystem.constraints.simple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class UnduplicateBindingConstraintImpl extends Constraint {

	private static final long serialVersionUID = 4244491933647460289L;

	@Override
	public void check(Context context, final Generic modified) throws AbstractConstraintViolationException {
		final Generic[] supers = ((GenericImpl) modified).getSupersArray();
		final Generic[] components = ((GenericImpl) modified).getComponentsArray();
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(components.length > 0 && components[0] != null ? ((AbstractContext) context).compositesIterator(components[0]) : ((AbstractContext) context).directInheritingsIterator(supers[0])) {
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
