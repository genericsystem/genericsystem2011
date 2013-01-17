package org.genericsystem.impl.constraints;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InheritanceDisabled;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.VirtualConstraintException;
import org.genericsystem.api.generic.Type;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@InheritanceDisabled
public class VirtualConstraintImpl extends Constraint {
	
	private static final long serialVersionUID = -5162099352671967024L;
	
	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (((Type) modified.getMeta()).isVirtualConstraintEnabled(context))
			throw new VirtualConstraintException(modified.getMeta() + "Problem should not be instanciated");
	}
	
	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
	
}