package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;

import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanSimpleConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void checkConsistency(Generic base, Generic attribute, AxedPropertyClass key, Serializable value) throws ConstraintViolationException {
		check(base, attribute, value);
	}

}
