package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanSimpleConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void check(Generic baseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		AbstractBooleanSimpleConstraintImpl constraint = (AbstractBooleanSimpleConstraintImpl) findAxedConstraint(key.getAxe());
		if (null != constraint)
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				constraint.check(baseComponent, inheriting);
	}

	public abstract void check(Generic baseComponent, Generic modified) throws ConstraintViolationException;
}
