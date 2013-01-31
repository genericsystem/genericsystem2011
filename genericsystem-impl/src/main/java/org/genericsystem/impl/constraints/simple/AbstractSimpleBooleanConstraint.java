package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;

/**
 * 
 * @author Michael Ory
 * @author Nicolas Feybesse
 * 
 */
public abstract class AbstractSimpleBooleanConstraint extends Constraint {

	private static final long serialVersionUID = 3553977162062086353L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		Snapshot<Value> constraintInstances = getConstraintInstances(context, modified, getClass());
		for (Value constraintValueNode : constraintInstances)
			check(context, modified, constraintValueNode);
	}

	private void check(Context context, Generic modified, Value constraintValueNode) throws ConstraintViolationException {
		if (!(constraintValueNode.getValue() instanceof Boolean))
			throw new ConstraintViolationException("The constraint " + getClass() + " must be a boolean constraint, the value is " + constraintValueNode.getValue());
		Boolean value = constraintValueNode.getValue();
		if (value == null)
			throw new ConstraintViolationException("The constraint " + getClass() + " must have a not null value");
		internalCheck(context, modified, constraintValueNode.<Type> getBaseComponent());
	}

	protected abstract void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException;

}