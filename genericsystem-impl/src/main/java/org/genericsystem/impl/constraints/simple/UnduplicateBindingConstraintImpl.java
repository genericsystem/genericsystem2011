package org.genericsystem.impl.constraints.simple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.AbstractContext;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class UnduplicateBindingConstraintImpl extends Constraint {

	private static final long serialVersionUID = 4244491933647460289L;

	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		final Generic[] supers = ((GenericImpl) modified).getSupersArray();
		final Generic[] components = ((GenericImpl) modified).getComponentsArray();
		Iterator<Generic> iterator = new AbstractFilterIterator<Generic>(components.length > 0 && components[0] != null ? ((AbstractContext) context).compositesIterator(components[0]) : ((AbstractContext) context).directInheritingsIterator(supers[0])) {
			@Override
			public boolean isSelected() {
				return Arrays.equals(((GenericImpl) next).getSupersArray(), supers) && Arrays.equals(((GenericImpl) next).getComponentsArray(), AbstractContext.transform(components, next)) && Objects.equals(modified.getValue(), next.getValue());
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
