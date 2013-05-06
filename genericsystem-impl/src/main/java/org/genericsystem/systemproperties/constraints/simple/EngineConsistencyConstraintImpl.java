package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.EngineConsistencyConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@Priority(Integer.MIN_VALUE)
public class EngineConsistencyConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = 8896806730580779746L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!modified.getEngine().equals(context.getEngine()))
			throw new EngineConsistencyConstraintViolationException("The Engine of " + modified + " isn't equals at Engine of the Context");
	}
}
