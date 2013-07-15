package org.genericsystem.systemproperties.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;

public abstract class AbstractSimpleConstraintImpl extends AbstractConstraintImpl {
	public abstract void check(Generic baseComponent, Generic modified) throws ConstraintViolationException;
}
