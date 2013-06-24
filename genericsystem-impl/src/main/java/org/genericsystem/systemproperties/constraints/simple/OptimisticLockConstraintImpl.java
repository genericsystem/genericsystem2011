package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@Priority(0)
public class OptimisticLockConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -9140332757904379387L;

	@Override
	public void check(Cache cache, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(cache, modified, getClass()).isEmpty())
			if (cache instanceof CacheImpl && ((CacheImpl) cache).isScheduledToRemove(modified) && (!((CacheImpl) cache).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
				throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

}