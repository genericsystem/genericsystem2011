package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Interfaces;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConcreteInheritanceConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@Dependencies(ConcreteInheritanceConstraintImpl.DefaultValue.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class ConcreteInheritanceConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		if (modified.isConcrete() && ((GenericImpl) modified).isPrimary() && !((GenericImpl) modified).isPhantom())
			if (((GenericImpl) modified).getSupers().first().isConcrete())
				throw new ConcreteInheritanceConstraintViolationException("" + modified);
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	@BooleanValue(true)
	@Interfaces(ConcreteInheritanceConstraintImpl.class)
	public static class DefaultValue {
	}

}
