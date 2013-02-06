package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.system.ComponentPosValue;

public abstract class AbstractAxedIntegerConstraint extends Constraint {

	private static final long serialVersionUID = 3553977162062086353L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : getConstraintValues(context, modified, getClass()))
			if (constraintValueNode.getValue() != null)
				internalCheck(context, modified, constraintValueNode.<Relation> getBaseComponent(), constraintValueNode.<ComponentPosValue<Boolean>> getValue().getComponentPos());
	}

	protected abstract void internalCheck(Context context, Generic modified, Relation constraintType, Integer axe) throws ConstraintViolationException;

}