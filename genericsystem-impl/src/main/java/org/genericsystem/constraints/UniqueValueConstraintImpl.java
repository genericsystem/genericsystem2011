package org.genericsystem.constraints;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
 * 
 * 
 * @author Nicolas Feybesse
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
// @Dependencies(UniqueValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	// @SystemGeneric
	// @Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	// @Components(UniqueValueConstraintImpl.class)
	// @BooleanValue(true)
	// public static class DefaultValue extends GenericImpl implements Holder {
	// }

	@Override
	public void check(Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		Set<Serializable> values = new HashSet<>();
		for (Generic attributeNode : ((Type) constraintBase).getAllInstances()) {
			if (attributeNode.getValue() != null && !values.add(attributeNode.getValue()))
				throw new UniqueValueConstraintViolationException("Duplicate value : " + attributeNode.getValue());
		}
	}

}
