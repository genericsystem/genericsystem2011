package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.impl.core.GenericImpl;

@SystemGeneric
// (defaultBehavior = true)
@Components(Engine.class)
@Dependencies(AliveConstraintImpl.DefaultValue.class)
@SingularConstraint
public class AliveConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		for (Generic generic : ((GenericImpl) modified).getComponents())
			if (generic != null && !generic.isAlive(context))
				throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");

		for (Generic generic : ((GenericImpl) modified).getSupers())
			if (!generic.isAlive(context))
				throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");

	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	// @BooleanValue(true)
	@ComponentPosBoolean(true)
	// @Supers(value = { AliveConstraintImpl.class }, implicitSuper = AliveConstraintImpl.class)
	public static class DefaultValue extends AliveConstraintImpl {

		private static final long serialVersionUID = 369915328786791901L;
	}

}
