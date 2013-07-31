package org.genericsystem.systemproperties.constraints.axed;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.RequiredConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(ConstraintKey.class)
@AxedConstraintValue(RequiredConstraintImpl.class)
@SingularConstraint
public class RequiredConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {

	@Override
	public void check(Generic base, Generic attribute, int axe) throws ConstraintViolationException {
		if (base.isConcrete()) {
			if (base.getHolders((Holder) attribute).isEmpty())
				throw new RequiredConstraintViolationException(attribute + " is required");
		} else {
			Snapshot<Generic> instances = ((Type) base).getAllInstances();
			if (instances.isEmpty())
				throw new RequiredConstraintViolationException(attribute + " is required");
			for (Generic generic : instances)
				if (null == generic.getHolder((Attribute) attribute, axe))
					throw new RequiredConstraintViolationException(generic + " is required for " + attribute);
		}
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE) || checkingType.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
