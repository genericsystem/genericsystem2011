package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Priority;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularInstanceConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
@Priority(10)
@InstanceClassConstraint(Boolean.class)
public class SingularInstanceConstraintImpl extends AbstractSimpleBooleanConstraint {
	
	private static final long serialVersionUID = -7689576125534105005L;
	
	@Override
	protected void internalCheck(Context context, Generic modified, Value constraintValueNode) throws ConstraintViolationException {
		int instanceNumber = ((Type) constraintValueNode).getAllInstances(context).size();
		if (instanceNumber > 1)
			throw new SingularInstanceConstraintViolationException("Singular instance constraint violation : type " + constraintValueNode + " has " + instanceNumber + " instances.");
	}
	
}