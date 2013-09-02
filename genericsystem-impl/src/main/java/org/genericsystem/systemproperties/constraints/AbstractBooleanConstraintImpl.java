package org.genericsystem.systemproperties.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractBooleanConstraintImpl extends AbstractConstraintImpl {
	@Override
	public void check(Generic modified, Holder valueBaseComponent) throws ConstraintViolationException {
		if (valueBaseComponent.getValue())
			check(modified, valueBaseComponent.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent());
	}

	public abstract void check(Generic modified, Generic baseComponent) throws ConstraintViolationException;
}
