package org.genericsystem.systemproperties.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractBooleanConstraintImpl extends AbstractConstraintImpl {
	@Override
	public void check(Generic modified, Holder valueBaseComponent, AxedPropertyClass key) throws ConstraintViolationException {
		if (valueBaseComponent.getValue()/* getCurrentCache().<Holder> find(ConstraintsMapProvider.ConstraintValue.class) */)
			check(modified, valueBaseComponent.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent(), key);
	}

	public abstract void check(Generic modified, Generic baseComponent, AxedPropertyClass key) throws ConstraintViolationException;
}
