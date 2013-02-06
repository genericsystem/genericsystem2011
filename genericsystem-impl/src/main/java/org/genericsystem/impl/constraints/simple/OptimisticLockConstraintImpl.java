package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Priority;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.CacheImpl;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@Priority(0)
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class OptimisticLockConstraintImpl extends Constraint {

	private static final long serialVersionUID = -9140332757904379387L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (modified.isSystemPropertyEnabled(context, getClass()))
			if (context instanceof CacheImpl && ((CacheImpl) context).isScheduledToRemove(modified) && (!((CacheImpl) context).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
				throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

}