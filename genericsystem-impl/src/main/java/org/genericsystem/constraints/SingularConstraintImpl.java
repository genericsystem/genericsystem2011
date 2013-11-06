package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@Dependencies(SingularConstraintImpl.DefaultValue.class)
@AxedConstraintValue(SingularConstraintImpl.class)
public class SingularConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(SingularConstraintImpl.class)
	@BooleanValue(false)
	public static class DefaultValue extends GenericImpl implements Holder {}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		Snapshot<Holder> holders = modified.getHolders((Attribute) constraintBase);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of attribute " + constraintBase + " on component " + modified + holders.get(0).info() + holders.get(1).info());

	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (((GenericImpl) modified).isPhantom() && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}

}