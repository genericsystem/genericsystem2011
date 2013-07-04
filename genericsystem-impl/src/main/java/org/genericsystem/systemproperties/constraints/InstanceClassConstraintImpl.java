package org.genericsystem.systemproperties.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@SingularConstraint
public class InstanceClassConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(final Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(modified.getMeta(), InstanceClassConstraintImpl.class)) {
			if (SystemGeneric.CONCRETE == modified.getMetaLevel() && ((GenericImpl) modified.getMeta()).getValue(((EngineImpl) modified.getEngine()).getCurrentCache().<Attribute> find(InstanceClassConstraintImpl.class)) != null) {
				Class<?> clazz = (Class<?>) constraintValue.getValue();
				if (modified.getValue() != null && !clazz.isAssignableFrom(modified.getValue().getClass()))
					throw new InstanceClassConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is " + modified.getValue().getClass().getSimpleName() + " for type "
							+ constraintValue.getConstraintBaseType().getValue());
			}
		}
	}

}
