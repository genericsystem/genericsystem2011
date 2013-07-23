package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanSimpleConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic type, AxedConstraintClass key) throws ConstraintViolationException {
		// for (Generic inheritingType : ((GenericImpl) type).getAllInheritings())
		check(modified, type);
	}

	@Override
	public void checkConsistency(Generic base, Holder valueBaseComponent, Generic attribute, AxedConstraintClass key) throws ConstraintViolationException {
		check(base, attribute);
	}

	public abstract void check(Generic modified, Generic type) throws ConstraintViolationException;
}
