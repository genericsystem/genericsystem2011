package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.SuperRuleConstraintViolationException;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class SuperRuleConstraintImpl extends Constraint {

	private static final long serialVersionUID = 6874090673594299362L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			for (Generic directSuper : modified.getSupers())
				if (!((GenericImpl) directSuper).isSuperOf(modified))
					throw new SuperRuleConstraintViolationException(modified.info() + " should inherits from : " + directSuper.info());
	}
}
