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
		if (valueConstraint.getValue()!=null && !Boolean.FALSE.equals(valueConstraint.getValue())) {
			//AxedPropertyClass key = getValue();
			check(axe == Statics.MULTIDIRECTIONAL ? modified : ((GenericImpl) modified).<GenericImpl> getComponent(axe), valueConstraint.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent(), axe);
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
