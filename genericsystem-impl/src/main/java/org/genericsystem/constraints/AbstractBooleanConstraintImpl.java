package org.genericsystem.constraints;

import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public abstract class AbstractBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic modified, Holder valueConstraint, int axe) throws ConstraintViolationException {
		if (valueConstraint.getValue()) {
			AxedPropertyClass key = getValue();
			check(key.getAxe() == Statics.MULTIDIRECTIONAL ? modified : ((GenericImpl) modified).<GenericImpl> getComponent(key.getAxe()), valueConstraint.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent(), key.getAxe());
		}
	}

	public abstract void check(Generic base, Generic baseConstraint, int axe) throws ConstraintViolationException;
}
