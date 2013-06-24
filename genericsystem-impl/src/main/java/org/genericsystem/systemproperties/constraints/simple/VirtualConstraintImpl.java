package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InheritanceDisabled;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.VirtualConstraintException;
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
@InheritanceDisabled
public class VirtualConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -5162099352671967024L;

	@Override
	public void check(Cache cache, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(cache, modified, getClass()).isEmpty())
			if (((Type) modified.getMeta()).isVirtualConstraintEnabled(cache))
				throw new VirtualConstraintException(modified.getMeta() + "Problem should not be instanciated");
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

}
