package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class InstanceClassConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, final Generic modified) throws AbstractConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified.getMeta(), InstanceClassConstraintImpl.class)) {
			if (SystemGeneric.CONCRETE == modified.getMetaLevel() && ((GenericImpl) modified.getMeta()).getValue(context, ((AbstractContext) context).<Attribute> find(InstanceClassConstraintImpl.class)) != null) {
				Class<?> clazz = (Class<?>) constraintValue.getComponentPosValue().getValue();
				if (modified.getValue() != null && !clazz.isAssignableFrom(modified.getValue().getClass()))
					throw new ClassInstanceConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is " + modified.getValue().getClass().getSimpleName() + " for type "
							+ constraintValue.getConstraintType().getValue());
			}
		}
	}

}
