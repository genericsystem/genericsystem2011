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
import org.genericsystem.exception.PhantomConstraintViolationException;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class PhantomConstraintImpl extends Constraint {

	private static final long serialVersionUID = -1175582355395269087L;

	// TODO KK
	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (modified.isAlive(context) && ((GenericImpl) modified.getSupers().first()).isPhantom())
			throw new PhantomConstraintViolationException(modified.info());
	}

}
