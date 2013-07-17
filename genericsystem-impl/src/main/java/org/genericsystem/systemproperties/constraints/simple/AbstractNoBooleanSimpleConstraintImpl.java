package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanSimpleConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void check(Generic baseComponent, AxedConstraintClass key, Serializable value) throws ConstraintViolationException {
		AbstractNoBooleanSimpleConstraintImpl constraint = (AbstractNoBooleanSimpleConstraintImpl) findAxedConstraint(key.getAxe());
		if (null != constraint)
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				constraint.check(baseComponent, inheriting, value);
	}

	public abstract void check(Generic baseComponent, Generic modified, Serializable value) throws ConstraintViolationException;
}
