package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractNoBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic modified, Holder valueBaseComponent) throws ConstraintViolationException {
		check(modified, valueBaseComponent.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent(), valueBaseComponent.getValue());
	}

	public abstract void check(Generic modified, Generic baseComponent, Serializable value) throws ConstraintViolationException;

}
