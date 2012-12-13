package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.AbstractConstraint;
import org.genericsystem.impl.core.GenericImpl;

public abstract class AbstractSimpleBooleanConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 3553977162062086353L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : ((GenericImpl) modified).getConstraintInstances(context, getClass())) {
			if (!(constraintValueNode.getValue() instanceof Boolean))
				throw new ConstraintViolationException("The constraint " + getClass() + " must be a boolean constraint, the value is " + constraintValueNode.getValue());
			Boolean value = constraintValueNode.getValue();
			if (value == null)
				throw new ConstraintViolationException("The constraint " + getClass() + " must have a not null value");
			internalCheck(context, modified, constraintValueNode.<Relation> getBaseComponent());
		}
	}

	protected abstract void internalCheck(Context context, Generic modified, Value constraintValueNode) throws ConstraintViolationException;

}