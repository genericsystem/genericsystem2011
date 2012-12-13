package org.genericsystem.impl.constraints.simple;

import static org.genericsystem.api.annotation.SystemGeneric.CONCRETE;

import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Interfaces;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PhantomConstraintViolationException;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@Dependencies(PhantomConstraintImpl.DefaultValue.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class PhantomConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -1175582355395269087L;

	@Override
	protected void internalCheck(Context context, Generic modified, Value constraintValueNode) throws ConstraintViolationException {
		if (modified.isAlive(context))
			if (((GenericImpl) modified.getImplicit().getSupers().first()).isPhantom())
				throw new PhantomConstraintViolationException(modified.info());
	}

	@SystemGeneric(CONCRETE)
	@Components(Engine.class)
	@BooleanValue(true)
	@Interfaces(PhantomConstraintImpl.class)
	public static class DefaultValue {
	}
}

