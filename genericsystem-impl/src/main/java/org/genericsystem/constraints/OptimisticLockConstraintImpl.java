//package org.genericsystem.systemproperties.constraints;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.Dependencies;
//import org.genericsystem.annotation.Extends;
//import org.genericsystem.annotation.Priority;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.constraints.SingularConstraint;
//import org.genericsystem.annotation.value.AxedConstraintValue;
//import org.genericsystem.annotation.value.BooleanValue;
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.CacheImpl;
//import org.genericsystem.core.EngineImpl;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.GenericImpl;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.exception.OptimisticLockConstraintViolationException;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.map.ConstraintsMapProvider;
//import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
//import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
//
///**
// * @author Nicolas Feybesse
// * 
// */
//@SystemGeneric
//@Extends(meta = ConstraintKey.class)
//@Components(MapInstance.class)
//@SingularConstraint
//@Dependencies(OptimisticLockConstraintImpl.DefaultValue.class)
//@AxedConstraintValue(OptimisticLockConstraintImpl.class)
//@Priority(0)
//public class OptimisticLockConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {
//
//	@SystemGeneric
//	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
//	@Components(OptimisticLockConstraintImpl.class)
//	@BooleanValue(true)
//	public static class DefaultValue extends GenericImpl implements Holder {
//	}
//
//	@Override
//	public void check(Generic modified, Generic type) throws ConstraintViolationException {
//		Cache cache = ((EngineImpl) modified.getEngine()).getCurrentCache();
//		if (cache instanceof CacheImpl && ((CacheImpl) cache).isScheduledToRemove(modified) && (!((CacheImpl) cache).getSubContext().isAlive(modified) || ((GenericImpl) modified).getLifeManager().willDie()))
//			throw new OptimisticLockConstraintViolationException("Generic : " + modified + " has already been removed by another thread");
//	}
//
//	@Override
//	public boolean isCheckedAt(Generic modified, CheckingType type) {
//		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
//	}
//
//}
package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanConstraintImpl;
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
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(OptimisticLockConstraintImpl.DefaultValue.class)
@AxedConstraintValue(OptimisticLockConstraintImpl.class)
@Priority(0)
public class OptimisticLockConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(OptimisticLockConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic instanceToCheck, Generic constraintBase) throws ConstraintViolationException {
		Cache cache = ((EngineImpl) constraintBase.getEngine()).getCurrentCache();
		if (cache instanceof CacheImpl && ((CacheImpl) cache).isScheduledToRemove(constraintBase) && (!((CacheImpl) cache).getSubContext().isAlive(constraintBase) || ((GenericImpl) constraintBase).getLifeManager().willDie()))
			throw new OptimisticLockConstraintViolationException("Generic : " + constraintBase + " has already been removed by another thread");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}
}