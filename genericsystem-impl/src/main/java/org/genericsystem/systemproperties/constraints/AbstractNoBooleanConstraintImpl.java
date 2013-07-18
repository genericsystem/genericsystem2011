package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;

public abstract class AbstractNoBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic modified, Holder valueBaseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		check(modified, valueBaseComponent.<Attribute> getBaseComponent().getBaseComponent(), key, valueBaseComponent.getValue(getCurrentCache().<Holder> find(ConstraintsMapProvider.ConstraintValue.class)));
	}

	public abstract void check(Generic modified, Generic baseComponent, AxedConstraintClass key, Serializable value) throws ConstraintViolationException;
}
