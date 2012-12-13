package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;

@SystemGeneric
@Components(Engine.class)
@InstanceClassConstraint(Integer.class)
public class SingularConstraintImpl extends AbstractAxedIntegerConstraint {
	
	private static final long serialVersionUID = 8068202813624343936L;
	
	// TODO do the same as PropertyConstraint
	
	@Override
	protected void internalCheck(Context context, Generic modified, Relation constraintType, Integer axe) throws ConstraintViolationException {
		Generic component = ((Link) modified).getComponent(axe);
		if (component.getLinks(context, constraintType, axe).size() > 1)
			throw new SingularConstraintViolationException("Multiple links of type " + constraintType + " on target " + component + " (n° " + axe + ").");
	}
	
}
