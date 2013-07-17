package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanAxedConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void check(Generic baseComponent, AxedConstraintClass key, Serializable value) throws ConstraintViolationException {
		AbstractNoBooleanAxedConstraintImpl constraint = (AbstractNoBooleanAxedConstraintImpl) findAxedConstraint(key.getAxe());
		if (null != constraint)
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				constraint.check(baseComponent, inheriting, key.getAxe(), value);
	}

	public abstract void check(Generic baseComponent, Generic modified, int pos, Serializable value) throws ConstraintViolationException;

}