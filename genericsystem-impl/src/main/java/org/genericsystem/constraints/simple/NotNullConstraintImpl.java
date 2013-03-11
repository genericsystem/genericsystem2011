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
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.NotNullConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class NotNullConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	// TODO KK
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (modified.getValue() == null)
				throw new NotNullConstraintViolationException("Holder should not be null for : " + ((Holder) modified).getMeta());
	}

}
