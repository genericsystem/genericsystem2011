package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SuperRuleConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
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
@Dependencies(SuperRuleConstraintImpl.DefaultValue.class)
@AxedConstraintValue(SuperRuleConstraintImpl.class)
public class SuperRuleConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(SuperRuleConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	// Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe
	@Override
	public void check(Generic instanceToCheck, Generic constraintBase, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		for (Generic directSuper : constraintBase.getSupers())
			if (!((GenericImpl) directSuper).isSuperOf(constraintBase))
				throw new SuperRuleConstraintViolationException(constraintBase.info() + " should inherits from : " + directSuper.info());
	}
}
