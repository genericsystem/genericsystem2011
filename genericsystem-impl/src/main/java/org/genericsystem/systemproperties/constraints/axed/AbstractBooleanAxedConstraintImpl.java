package org.genericsystem.systemproperties.constraints.axed;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanAxedConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic baseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		AbstractBooleanAxedConstraintImpl constraint = (AbstractBooleanAxedConstraintImpl) findAxedConstraint(key.getAxe());
		if (null != constraint)
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				constraint.check(modified, inheriting, key.getAxe());
	}

	public abstract void check(Generic modified, Generic baseComponent, int axe) throws ConstraintViolationException;

}