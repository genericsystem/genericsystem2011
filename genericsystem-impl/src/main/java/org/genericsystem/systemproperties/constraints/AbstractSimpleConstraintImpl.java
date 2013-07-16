package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractSimpleConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Holder valueBaseComponent, Serializable key, Class<? extends Serializable> keyClazz) throws ConstraintViolationException {
		Generic baseComponent = valueBaseComponent != null ? valueBaseComponent.<Attribute> getBaseComponent().getBaseComponent() : null;
		if (isBooleanConstraintEnabledOrNotBoolean(valueBaseComponent, keyClazz))
			for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
				check(baseComponent, inheriting);
	}

	public abstract void check(Generic baseComponent, Generic modified) throws ConstraintViolationException;
}
