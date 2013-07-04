package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueConstraintViolationException;
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
public class UniqueConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(modified, getClass())) {
			Type constraintBaseType = (Type) constraintValue.getConstraintBaseType();
			for (Generic generic : constraintBaseType.getAllInstances())
				if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
					throw new UniqueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + constraintBaseType + ".");
		}
	}
}
