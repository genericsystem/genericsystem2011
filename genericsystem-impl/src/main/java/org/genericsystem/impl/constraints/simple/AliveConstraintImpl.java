package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class AliveConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (modified.isSystemPropertyEnabled(context, getClass())) {
			for (Generic generic : ((GenericImpl) modified).getComponents())
				if (generic != null && !generic.isAlive(context))
					throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");
			for (Generic generic : ((GenericImpl) modified).getSupers())
				if (!generic.isAlive(context))
					throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
		}

	}

}
