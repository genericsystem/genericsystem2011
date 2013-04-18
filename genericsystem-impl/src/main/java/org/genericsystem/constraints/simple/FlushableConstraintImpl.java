package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Transaction;
import org.genericsystem.exception.ConstraintViolationException;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@NotNullConstraint
public class FlushableConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (context instanceof Transaction)
			if (!((Transaction) context).isFlushable(modified))
				assert false;
		// throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
