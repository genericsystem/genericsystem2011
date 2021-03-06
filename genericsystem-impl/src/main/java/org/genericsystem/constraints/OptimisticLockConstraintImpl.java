package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ OptimisticLockConstraintImpl.DefaultKey.class, OptimisticLockConstraintImpl.DefaultValue.class })
@Priority(0)
public class OptimisticLockConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Meta(OptimisticLockConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(OptimisticLockConstraintImpl.class)
	public static class DefaultKey {
	}

	@SystemGeneric
	@Meta(ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {
	}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		Cache cache = ((EngineImpl) modified.getEngine()).getCurrentCache();
		if (cache instanceof CacheImpl && ((CacheImpl) cache).isScheduledToRemove(modified) && (!((CacheImpl) cache).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
			throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}
}