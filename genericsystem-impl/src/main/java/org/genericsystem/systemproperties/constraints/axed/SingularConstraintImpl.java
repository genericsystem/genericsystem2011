package org.genericsystem.systemproperties.constraints.axed;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
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
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic base, Generic attribute, int axe) throws ConstraintViolationException {
		Snapshot<Holder> holders = base.getHolders((Relation) attribute, axe);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of attribute " + attribute + " on component " + base + " (n° " + axe + ") : " + holders);
		for (Generic generic : ((GenericImpl) base).getAllInheritings()) {
			holders = generic.getHolders((Relation) attribute, axe);
			if (holders.size() > 1)
				throw new SingularConstraintViolationException("Multiple links of attribute " + attribute + " on component " + base + " (n° " + axe + ") : " + holders);
		}
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}
}