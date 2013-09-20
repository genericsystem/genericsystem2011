package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;
import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.AbstractNoBooleanConstraintImpl;

public abstract class AbstractNoBooleanAxedConstraintImpl extends AbstractNoBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic attribute, Serializable value) throws ConstraintViolationException {
		AxedPropertyClass key = getValue();
		if (key.getAxe() != Statics.MULTIDIRECTIONAL)
			check(((GenericImpl) modified).<GenericImpl> getComponent(key.getAxe()), attribute, key.getAxe(), value);
	}

	@Override
	public void checkConsistency(Generic base, Generic attribute, AxedPropertyClass key, Serializable value) throws ConstraintViolationException {
		check(base, attribute, key.getAxe(), value);
	}

	public abstract void check(Generic base, Generic attribute, int pos, Serializable value) throws ConstraintViolationException;

}
