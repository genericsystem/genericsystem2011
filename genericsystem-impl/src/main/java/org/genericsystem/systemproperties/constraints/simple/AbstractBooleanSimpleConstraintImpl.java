package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanSimpleConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic baseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
			check(modified, inheriting);
	}

	public abstract void check(Generic modified, Generic baseComponent) throws ConstraintViolationException;
}
