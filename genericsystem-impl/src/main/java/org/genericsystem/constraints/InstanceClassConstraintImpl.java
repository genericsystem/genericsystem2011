package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class InstanceClassConstraintImpl extends AbstractConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Holder constraintValue) throws ConstraintViolationException {
		for (Generic instance : ((Attribute) getConstraintBase(constraintValue)).getInstances())
			if (!constraintValue.<Class<?>> getValue().isAssignableFrom(instance.getValue().getClass()))
				throw new InstanceClassConstraintViolationException(instance.getValue() + " should be " + constraintValue.getValue());
	}
}
