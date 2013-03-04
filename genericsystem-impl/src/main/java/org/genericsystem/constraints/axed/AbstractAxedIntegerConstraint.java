package org.genericsystem.constraints.axed;

import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.generic.Relation;

public abstract class AbstractAxedIntegerConstraint extends Constraint {

	private static final long serialVersionUID = 3553977162062086353L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass()))
			internalCheck(context, modified, (Relation) constraintValue.getConstraintType(), constraintValue.getComponentPosValue().getComponentPos());
	}

	protected abstract void internalCheck(Context context, Generic modified, Relation constraintType, Integer axe) throws AbstractConstraintViolationException;

}