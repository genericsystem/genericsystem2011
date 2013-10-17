package org.genericsystem.systemproperties.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
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
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@AxedConstraintValue(RequiredConstraintImpl.class)
@SingularConstraint
public class RequiredConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

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
				if (null == generic.getHolder(Statics.CONCRETE, (Attribute) attribute, axe))
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

	@Override
	public void checkConsistency(Generic base, Holder value, int axe)
			throws ConstraintViolationException {
		Type type = ((Attribute) base).getComponent(axe);
		for (Generic instance : type.getAllInstances()) {
			if (instance.getHolders((Attribute) base).isEmpty())
				throw new RequiredConstraintViolationException(instance
						+ " is required for " + base);
		}
	}
}