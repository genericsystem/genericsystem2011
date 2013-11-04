package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.AxedPropertyClass;
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
@AxedConstraintValue(InstanceClassConstraintImpl.class)
public class InstanceClassConstraintImpl extends AbstractConstraintImpl implements Holder {

	@Override
	public void check(Generic instanceToCheck, Generic constraintBase, Holder constraintValue, int axe) throws ConstraintViolationException {
		for (Generic instance : ((Attribute) instanceToCheck).getInstances())
			if (!constraintValue.getValue().equals(AxedPropertyClass.class) && !instance.getValue().getClass().equals(constraintValue.getValue()))
				throw new InstanceClassConstraintViolationException("Wrong type of instance");
	}
}
