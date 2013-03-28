package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularInstanceConstraintViolationException;
import org.genericsystem.generic.Type;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@Priority(10)
@NotNullConstraint
public class SingularInstanceConstraintImpl extends Constraint {

	private static final long serialVersionUID = -7689576125534105005L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass())) {
			Type constraintBaseType = (Type) constraintValue.getConstraintType();
			int instanceNumber = constraintBaseType.getAllInstances(context).size();
			if (instanceNumber > 1)
				throw new SingularInstanceConstraintViolationException("Singular instance constraint violation : type " + constraintBaseType + " has " + instanceNumber + " instances.");
		}
	}

}