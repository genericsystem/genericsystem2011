package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InheritanceDisabledConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.VirtualConstraintException;
import org.genericsystem.generic.Type;
import org.genericsystem.system.BooleanSystemProperty;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InheritanceDisabledConstraint
@NotNullConstraint
public class VirtualConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -5162099352671967024L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (((Type) modified.getMeta()).isVirtualConstraintEnabled(context))
				throw new VirtualConstraintException(modified.getMeta() + "Problem should not be instanciated");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

}
