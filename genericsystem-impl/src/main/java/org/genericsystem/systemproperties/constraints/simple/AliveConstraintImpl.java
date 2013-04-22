package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@NotNullConstraint
public class AliveConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty()) {
			for (Generic generic : ((GenericImpl) modified).getComponents())
				if (!generic.isAlive(context))
					throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");
			for (Generic generic : ((GenericImpl) modified).getSupers())
				if (!generic.isAlive(context))
					throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
		}
	}

}
