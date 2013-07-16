package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractSimpleConstraintImpl extends AbstractConstraintImpl {

	@SuppressWarnings("unchecked")
	@Override
	public void check(Holder valueBaseComponent, AxedConstraintClass key) throws ConstraintViolationException {
		AbstractSimpleConstraintImpl constraint = (AbstractSimpleConstraintImpl) findAxedConstraint(key.getAxe());
		Generic baseComponent = valueBaseComponent != null ? valueBaseComponent.<Attribute> getBaseComponent().getBaseComponent() : null;
		if (isBooleanConstraintEnabledOrNotBoolean(valueBaseComponent, (Class<? extends Serializable>) key.getClazz()))
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				constraint.check(baseComponent, inheriting);
	}

	public abstract void check(Generic baseComponent, Generic modified) throws ConstraintViolationException;
}
