package org.genericsystem.impl.constraints;

import java.io.Serializable;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.AbstractContext;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
public class InstanceClassConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : getConstraintValues(context, modified.getMeta(), InstanceClassConstraintImpl.class)) {
			if (SystemGeneric.CONCRETE == modified.getMetaLevel() && ((GenericImpl) modified.getMeta()).getValue(context, ((AbstractContext) context).<Attribute> find(InstanceClassConstraintImpl.class)) != null) {
				Class<?> clazz = (Class<?>) constraintValueNode.<ComponentPosValue<Serializable>> getValue().getValue();
				if (modified.getValue() != null && !clazz.isAssignableFrom(modified.getValue().getClass()))
					throw new ClassInstanceConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is " + modified.getValue().getClass().getSimpleName() + " for type "
							+ constraintValueNode.getBaseComponent().getValue());
			}
		}
	}

}
