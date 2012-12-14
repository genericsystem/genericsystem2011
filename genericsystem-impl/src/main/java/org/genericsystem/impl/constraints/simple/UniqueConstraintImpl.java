package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.UniqueConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
@InstanceClassConstraint(Boolean.class)
public class UniqueConstraintImpl extends AbstractSimpleBooleanConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		for (Generic generic : ((Type) constraintBaseType).getAllInstances(context))
			if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
				throw new UniqueConstraintViolationException("Value " + modified.getValue() + " is duplicate for type " + constraintBaseType + ".");
	}
	
}
