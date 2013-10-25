package org.genericsystem.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.AxedPropertyClass;

public abstract class AbstractAxedConstraint extends AbstractConstraintImpl {
	@Override
	public void check(Generic constraintBase, Generic modified, CheckingType checkingType, Holder constraintValue) throws ConstraintViolationException {
		//
		if (constraintValue.getValue() != null && !Boolean.FALSE.equals(constraintValue.getValue())) {
			AxedPropertyClass key = getValue();
			// Generic constraintBase = constraintValue.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent();
			check(constraintBase, modified, constraintValue);
		}
	}

	public abstract void check(Generic constraintBase, Generic modified, Holder constraintValue) throws ConstraintViolationException;;
}
