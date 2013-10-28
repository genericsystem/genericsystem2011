package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.VirtualConstraintException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
import org.genericsystem.map.ConstraintsMapProvider.NoInheritanceConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = NoInheritanceConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(VirtualConstraintImpl.class)
public class VirtualConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@Override
	public void check(Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		if (!((Type) constraintBase).getInstances().isEmpty())
			throw new VirtualConstraintException(modified + "  should not be instanciated");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
}
