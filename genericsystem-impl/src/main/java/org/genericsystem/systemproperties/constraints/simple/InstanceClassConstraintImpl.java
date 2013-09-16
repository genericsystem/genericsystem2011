package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
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
public class InstanceClassConstraintImpl extends AbstractNoBooleanSimpleConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic type, Serializable value) throws ConstraintViolationException {
		if (Statics.CONCRETE == modified.getMetaLevel() && ((GenericImpl) modified.getMeta()).getValue(((EngineImpl) modified.getEngine()).getCurrentCache().<Attribute> find(InstanceClassConstraintImpl.class)) != null) {
			Class<?> clazz = (Class<?>) value;
			if (modified.getValue() != null && !clazz.isAssignableFrom(modified.getValue().getClass()))
				throw new InstanceClassConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is " + modified.getValue().getClass().getSimpleName() + " for type " + type);
		}
	}

}