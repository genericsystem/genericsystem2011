package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.AbstractConstraint;
import org.genericsystem.impl.core.GenericImpl;

public abstract class AbstractAxedIntegerConstraint extends AbstractConstraint {

	private static final long serialVersionUID = 3553977162062086353L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : ((GenericImpl) modified).getConstraintInstances(context, getClass())) {
			if (!(constraintValueNode.getValue() instanceof Integer))
				throw new ConstraintViolationException("The constraint " + getClass() + " must be axed");
			Integer axe = constraintValueNode.getValue();
			if (axe == null)
				throw new ConstraintViolationException("The constraint " + getClass() + " must have a not null value");
			internalCheck(context, modified, constraintValueNode.<Relation> getBaseComponent(), axe);
		}
	}

	protected abstract void internalCheck(Context context, Generic modified, Relation constraintType, Integer axe) throws ConstraintViolationException;

}