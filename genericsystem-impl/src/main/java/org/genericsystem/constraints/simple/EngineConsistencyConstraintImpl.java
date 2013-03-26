package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.EngineConsistencyConstraintViolationException;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@NotNullConstraint
public class EngineConsistencyConstraintImpl extends Constraint {

	private static final long serialVersionUID = 8896806730580779746L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (!modified.getEngine().equals(context.getEngine()))
				throw new EngineConsistencyConstraintViolationException("The Engine of " + modified + " isn't equals at Engine of the Context");
	}
}
