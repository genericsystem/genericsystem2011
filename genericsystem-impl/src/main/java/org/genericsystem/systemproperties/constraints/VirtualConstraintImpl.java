package org.genericsystem.systemproperties.constraints;

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
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class, value = NoInheritanceSystemType.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(VirtualConstraintImpl.class)
public class VirtualConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic type,int axe) throws ConstraintViolationException {
		if (((Type) modified.getMeta()).equals(type) && ((Type) modified.getMeta()).isVirtualConstraintEnabled())
			throw new VirtualConstraintException(modified.getMeta() + " Problem should not be instanciated");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
	@Override
	public void checkConsistency(Generic modified, Holder valueConstraint, int axe) throws ConstraintViolationException {
		Generic baseConstraint = ((Holder) getBaseComponent()).getBaseComponent();
		if (!((Type) baseConstraint).getInstances().isEmpty())
			throw new VirtualConstraintException(baseConstraint + "  should not be instanciated");
	}
}
