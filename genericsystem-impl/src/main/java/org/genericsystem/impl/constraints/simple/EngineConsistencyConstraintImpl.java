package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.EngineConsistencyConstraintViolationException;

@SystemGeneric
@Components(Engine.class)
@Dependencies(EngineConsistencyConstraintImpl.DefaultValue.class)
@SingularConstraint
public class EngineConsistencyConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = 8896806730580779746L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		if (!modified.getEngine().equals(context.getEngine()))
			throw new EngineConsistencyConstraintViolationException("The Engine of " + modified + " isn't equals at Engine of the Context");
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	// @BooleanValue(true)
	@ComponentPosBoolean(true)
	@Supers(value = { EngineConsistencyConstraintImpl.class }, implicitSuper = EngineConsistencyConstraintImpl.class)
	public static class DefaultValue {
	}

}
