package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingletonConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
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
@AxedConstraintValue(SingletonConstraintImpl.class)
@Priority(10)
public class SingletonConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@Override
	public void check(Generic instanceToCheck, Generic constraintBase) throws ConstraintViolationException {
		if (((Type) instanceToCheck).getAllInstances().size() > 1)
			throw new SingletonConstraintViolationException("Singleton constraint violation : type " + constraintBase + " has " + ((Type) constraintBase).getAllInstances().size() + " instances.");
	}
}