package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Priority;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularInstanceConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@Priority(10)
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class SingularInstanceConstraintImpl extends Constraint {

	private static final long serialVersionUID = -7689576125534105005L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		Snapshot<Value> constraintInstances = getConstraintValues(context, modified, getClass());
		for (Value constraintValueNode : constraintInstances) {
			Type constraintBaseType = constraintValueNode.<Type> getBaseComponent();
			int instanceNumber = constraintBaseType.getAllInstances(context).size();
			if (instanceNumber > 1)
				throw new SingularInstanceConstraintViolationException("Singular instance constraint violation : type " + constraintBaseType + " has " + instanceNumber + " instances.");
		}
	}

}