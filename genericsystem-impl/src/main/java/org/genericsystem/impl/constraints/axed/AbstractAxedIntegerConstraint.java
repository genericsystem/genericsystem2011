package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;

public abstract class AbstractAxedIntegerConstraint extends Constraint {
	
	private static final long serialVersionUID = 3553977162062086353L;
	
	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : getConstraintInstances(context, modified, getClass())) {
			if (!(constraintValueNode.getValue() instanceof Integer))
				throw new ConstraintViolationException("The constraint " + getClass() + " must be axed");
			Integer componentPos = constraintValueNode.getValue();
			if (componentPos == null)
				throw new ConstraintViolationException("The constraint " + getClass() + " must have a not null value");
			internalCheck(context, modified, constraintValueNode.<Relation> getBaseComponent(), componentPos);
		}
	}
	
	protected abstract void internalCheck(Context context, Generic modified, Relation constraintType, Integer axe) throws ConstraintViolationException;
	
}