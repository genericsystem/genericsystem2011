package org.genericsystem.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.AxedPropertyClass;

public abstract class AbstractAxedConstraint extends AbstractConstraintImpl {
	@Override
	public void check(Generic constraintBase, Generic modified, CheckingType checkingType, Holder constraintValue) throws ConstraintViolationException {
		if (constraintValue.getValue() != null && !Boolean.FALSE.equals(constraintValue.getValue()))
			check(constraintBase, modified, constraintValue, this.<AxedPropertyClass> getValue().getAxe());
	}

	public abstract void check(Generic constraintBase, Generic modified, Holder constraintValue, int axe) throws ConstraintViolationException;;
}
