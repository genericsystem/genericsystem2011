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
import org.genericsystem.api.exception.PhantomConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.system.ComponentPosValue;

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
