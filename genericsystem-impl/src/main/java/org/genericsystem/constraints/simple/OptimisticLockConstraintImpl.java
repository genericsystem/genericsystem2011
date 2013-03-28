package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@Priority(0)
@NotNullConstraint
public class OptimisticLockConstraintImpl extends Constraint {

	private static final long serialVersionUID = -9140332757904379387L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (context instanceof CacheImpl && ((CacheImpl) context).isScheduledToRemove(modified) && (!((CacheImpl) context).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
				throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

}