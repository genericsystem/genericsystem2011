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

	/**
	 * Checks that modififed generic is unique on base component.
	 * 
	 * @param modified - modified generic.
	 * @param baseComponent - base component (type or another generic that contains modified).
	 * 
	 * @throws ConstraintViolationException
	 */
	public abstract void check(Generic modified, Generic baseComponent) throws ConstraintViolationException;
}
