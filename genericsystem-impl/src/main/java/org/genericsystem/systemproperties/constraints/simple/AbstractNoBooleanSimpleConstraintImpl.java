package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanSimpleConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic type, AxedConstraintClass key, Serializable value) throws ConstraintViolationException {
		// for (Generic inheritingType : ((GenericImpl) type).getAllInheritings())
		check(modified, type, value);
	}

	public abstract void check(Generic modified, Generic type, Serializable value) throws ConstraintViolationException;
}
