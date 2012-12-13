package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.NotNullConstraintViolationException;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class NotNullConstraintImpl extends AbstractSimpleBooleanConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@Override
	protected void internalCheck(Context context, Generic modified, Value constraintValueNode) throws ConstraintViolationException {
		if (modified.getValue() == null)
			throw new NotNullConstraintViolationException("Value should not be null for relation " + ((Value) modified).getMeta());
	}
	
}
