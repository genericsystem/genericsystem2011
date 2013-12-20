package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SizeConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class SizeConstraintImpl extends AbstractAxedConstraintImpl implements Holder {

	@Override
	public void internalCheck(Generic modified, Holder constraintValue) throws ConstraintViolationException {
		Generic constraintBase = getConstraintBase(constraintValue);
		if (constraintValue.getValue() instanceof Integer)
			if (modified.getHolders((Attribute) constraintBase).size() != (Integer) (constraintValue).getValue())
				throw new SizeConstraintViolationException("Multiple links of " + constraintBase + ", and the maximum size is " + constraintValue);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
