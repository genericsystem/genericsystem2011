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
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(ConstraintKey.class)
@Dependencies(SingularConstraintImpl.DefaultValue.class)
@AxedConstraintValue(SingularConstraintImpl.class)
public class SingularConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(SingularConstraintImpl.class)
	@Extends(ConstraintsMapProvider.ConstraintValue.class)
	@BooleanValue(false)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}

	@Override
	public void check(Generic modified, Generic baseComponent, int axe) throws ConstraintViolationException {
		Generic component = ((Link) modified).getComponent(axe);
		Snapshot<Holder> holders = ((GenericImpl) component).getHolders((Relation) baseComponent, axe);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of attribute " + baseComponent + " on component " + component + " (n° " + axe + ") : " + holders);
	}
}