package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingletonConstraintViolationException;
import org.genericsystem.generic.Type;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@Priority(10)
public class SingletonConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -7689576125534105005L;

	@Override
	public void check(Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(modified, getClass())) {
			Type constraintBaseType = (Type) constraintValue.getConstraintBaseType();
			int instanceNumber = constraintBaseType.getAllInstances().size();
			if (instanceNumber > 1)
				throw new SingletonConstraintViolationException("Singular instance constraint violation : type " + constraintBaseType + " has " + instanceNumber + " instances.");
		}
	}

}