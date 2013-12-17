package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class RequiredConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return CheckingType.CHECK_ON_REMOVE_NODE.equals(checkingType) || CheckingType.CHECK_ON_ADD_NODE.equals(checkingType);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		if (modified.getHolders((Attribute) constraintBase).isEmpty())
			throw new RequiredConstraintViolationException(modified + " is required for " + constraintBase);
	}

}
