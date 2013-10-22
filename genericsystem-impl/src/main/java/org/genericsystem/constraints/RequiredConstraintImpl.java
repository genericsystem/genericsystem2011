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
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class, value = NoInheritanceSystemType.class)
@Components(MapInstance.class)
@AxedConstraintValue(RequiredConstraintImpl.class)
@SingularConstraint
public class RequiredConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@Override
	public void check(Generic base, Generic attribute, int axe) throws ConstraintViolationException {
		if (base.isConcrete()) {
			if (base.getHolders((Holder) attribute).isEmpty())
				throw new RequiredConstraintViolationException(attribute + " is required");
		} else
			for (Generic instance : ((Type) base).getInstances())
				if (null == instance.getValue((Attribute) attribute))
					throw new RequiredConstraintViolationException(attribute + " is required for " + instance);
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
