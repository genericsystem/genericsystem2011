package org.genericsystem.systemproperties.constraints;

import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.systemproperties.BooleanSystemProperty;

public abstract class AbstractBooleanConstraintImpl extends AbstractConstraintImpl {
	@Override
	public void check(Holder valueBaseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		boolean checkable = true;
		if (BooleanSystemProperty.class.isAssignableFrom(key.getClazz()))
			checkable = valueBaseComponent.getValue(getCurrentCache().<Holder> find(ConstraintsMapProvider.ConstraintValue.class));
		if (checkable)
			check(valueBaseComponent.<Attribute> getBaseComponent().getBaseComponent(), key);
	}

	public abstract void check(Generic baseComponent, AxedConstraintClass key) throws ConstraintViolationException;
}
