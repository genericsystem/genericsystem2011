package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.AbstractConstraintViolationException;
import org.genericsystem.api.exception.EngineConsistencyConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class EngineConsistencyConstraintImpl extends Constraint {

	private static final long serialVersionUID = 8896806730580779746L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (!modified.getEngine().equals(context.getEngine()))
			throw new EngineConsistencyConstraintViolationException("The Engine of " + modified + " isn't equals at Engine of the Context");
	}
}
