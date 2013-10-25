package org.genericsystem.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.AxedPropertyClass;

public abstract class AbstractNoBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic constraintBase, Generic modified, CheckingType checkingType, Holder constraintValue) throws ConstraintViolationException {
		AxedPropertyClass key = getValue();
		// check(key.getAxe() == Statics.MULTIDIRECTIONAL ? modified : ((GenericImpl) modified).<GenericImpl> getComponent(key.getAxe()), constraintValue, checkingType, key.getAxe());
		// Generic constraintBase = constraintValue.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent();
		check(constraintBase, modified, constraintValue, checkingType, key.getAxe());
	}

	public abstract void check(Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException;

}
