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

	/**
	 * Checks that modififed generic is unique on base component.
	 * 
	 * @param modified - modified generic.
	 * @param baseComponent - base component (type or another generic that contains modified).
	 * @param axe - axe of constraint.
	 * 
	 * @throws ConstraintViolationException
	 */
	public abstract void check(Generic base, Generic baseConstraint, int axe) throws ConstraintViolationException;
}
