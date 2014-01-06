package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingletonConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Priority(10)
public class SingletonConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		if (((Type) constraintBase).getAllInstances().size() > 1)
			throw new SingletonConstraintViolationException("Singleton constraint violation : type " + modified + " has " + ((Type) modified).getAllInstances().size() + " instances.");
	}
}
