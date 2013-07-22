package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(ConstraintKey.class)
@SingularConstraint
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		for (Generic generic : ((Type) type).getAllInstances())
			if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
				throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
	}
}
