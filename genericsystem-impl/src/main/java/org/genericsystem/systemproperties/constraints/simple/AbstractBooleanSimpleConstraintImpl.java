package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanSimpleConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void checkConsistency(Generic base, Holder value, int axe) throws ConstraintViolationException {
		// check(base, attribute);
	}

}
