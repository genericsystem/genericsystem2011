package org.genericsystem.constraints;

import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;

public abstract class AbstractNoBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic modified, Holder valueConstraint, int axe) throws ConstraintViolationException {
		AxedPropertyClass key = getValue();
		check(key.getAxe() == Statics.MULTIDIRECTIONAL ? modified : ((GenericImpl) modified).<GenericImpl> getComponent(key.getAxe()), (Generic) valueConstraint, key.getAxe());
	}

	// baseconstraint= valueConstraint.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent()
	public abstract void check(Generic base, Generic valueConstraint, int axe) throws ConstraintViolationException;

}
