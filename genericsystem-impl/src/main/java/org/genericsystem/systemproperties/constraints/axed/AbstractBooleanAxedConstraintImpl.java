package org.genericsystem.systemproperties.constraints.axed;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.constraints.AbstractBooleanConstraintImpl;

public abstract class AbstractBooleanAxedConstraintImpl extends AbstractBooleanConstraintImpl {

	@Override
	public void check(Generic modified, Generic attribute, AxedConstraintClass key) throws ConstraintViolationException {
		if (key.getAxe() != Statics.NO_POSITION)
			// for (Generic base : ((GenericImpl) attribute).<GenericImpl> getComponent(key.getAxe()).getAllInheritings())
			check(((GenericImpl) modified).<GenericImpl> getComponent(key.getAxe()), attribute, key.getAxe());
	}

	@Override
	public void checkConsistency(Generic base, Holder valueBaseComponent, Generic attribute, AxedConstraintClass key) throws ConstraintViolationException {
		GenericImpl component = ((GenericImpl) attribute).<GenericImpl> getComponent(key.getAxe());
		if (null != component)
			for (Generic inheriting : component.getAllInheritings())
				check(inheriting, attribute, key.getAxe());
	}

	public abstract void check(Generic base, Generic attribute, int axe) throws ConstraintViolationException;

}