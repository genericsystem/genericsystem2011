package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanAxedConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic baseComponent, AxedConstraintClass key, Serializable value) throws ConstraintViolationException {
		for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
			check(modified, inheriting, key.getAxe(), value);
	}

	public abstract void check(Generic modified, Generic baseComponent, int pos, Serializable value) throws ConstraintViolationException;

}