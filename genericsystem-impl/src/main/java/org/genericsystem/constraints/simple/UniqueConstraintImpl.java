package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.UniqueConstraintViolationException;
import org.genericsystem.generic.Type;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class UniqueConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass())) {
			Type constraintBaseType = (Type) constraintValue.getConstraintType();
			for (Generic generic : constraintBaseType.getAllInstances(context))
				if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
					throw new UniqueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + constraintBaseType + ".");
		}
	}
}
