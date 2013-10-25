package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@AxedConstraintValue(RequiredConstraintImpl.class)
@SingularConstraint
public class RequiredConstraintImpl extends AbstractAxedConstraint implements Holder {

	// @Override
	// public void check(Generic instanceToCheck, Generic constraintBase, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
	//
	// if (CheckingType.CHECK_ON_REMOVE_NODE.equals(checkingType)) {
	// if (instanceToCheck.getHolders((Attribute) constraintBase, axe).isEmpty())
	// throw new RequiredConstraintViolationException(instanceToCheck + " is required for " + constraintBase);
	// }

	//
	// for (Generic instance : ((Type) instanceToCheck).getAllInstances())
	// if (instance.getHolders((Attribute) constraintBase, axe).isEmpty() && CheckingType.CHECK_ON_ADD_NODE.equals(checkingType))
	// throw new RequiredConstraintViolationException(constraintBase + " is required for " + instance);
	//
	// }

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return CheckingType.CHECK_ON_REMOVE_NODE.equals(checkingType) || CheckingType.CHECK_ON_ADD_NODE.equals(checkingType);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

	@Override
	public void check(Generic constraintBase, Generic modified, Holder constraintValue) throws ConstraintViolationException {
		log.info("@@@@@@@@@@ modified  " + modified + "" + constraintBase);
		if (modified.getHolders((Attribute) constraintBase).isEmpty())
			throw new RequiredConstraintViolationException("" + modified + " is required for " + constraintValue);
	}

}
