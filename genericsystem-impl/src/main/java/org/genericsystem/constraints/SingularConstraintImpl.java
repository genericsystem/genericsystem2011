package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class SingularConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		Snapshot<Holder> holders = modified.getHolders((Attribute) constraintBase);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of attribute " + constraintBase + " on component " + modified + holders.get(0).info() + holders.get(1).info());
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

}