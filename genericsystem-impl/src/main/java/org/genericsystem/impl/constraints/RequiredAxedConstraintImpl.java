package org.genericsystem.impl.constraints;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.impl.constraints.axed.AbstractAxedIntegerConstraint;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class RequiredAxedConstraintImpl extends AbstractAxedIntegerConstraint {

	private static final long serialVersionUID = 2837810754525623146L;

	@Override
	protected void internalCheck(Context context, Generic required, Relation constraintType, Integer axe) throws ConstraintViolationException {
		Generic component = ((Relation) required).getComponent(axe);
		if (component.getLinks(context, constraintType, axe).size() < 1)
			throw new RequiredConstraintViolationException(required + " have no required " + component + " on axe (n° " + axe + ").");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE) || type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
