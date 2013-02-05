package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Priority;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.impl.core.CacheImpl;
import org.genericsystem.impl.core.GenericImpl;

@SystemGeneric
@Components(Engine.class)
@Dependencies(OptimisticLockConstraintImpl.DefaultValue.class)
@Priority(0)
@SingularConstraint
public class OptimisticLockConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -9140332757904379387L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		if (context instanceof CacheImpl && ((CacheImpl) context).isScheduledToRemove(modified) && (!((CacheImpl) context).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
			throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	// @BooleanValue(true)
	@ComponentPosBoolean(true)
	@Supers(value = { OptimisticLockConstraintImpl.class }, implicitSuper = OptimisticLockConstraintImpl.class)
	public static class DefaultValue {
	}
}