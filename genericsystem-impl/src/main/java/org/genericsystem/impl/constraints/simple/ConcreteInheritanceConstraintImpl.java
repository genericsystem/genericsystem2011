package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConcreteInheritanceConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.impl.core.GenericImpl;

@SystemGeneric
@Components(Engine.class)
@Dependencies(ConcreteInheritanceConstraintImpl.DefaultValue.class)
@SingularConstraint
public class ConcreteInheritanceConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
			if (((GenericImpl) modified).getSupers().first().isConcrete())
				throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified);
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	// @BooleanValue(true)
	@ComponentPosBoolean(true)
	@Supers(value = { ConcreteInheritanceConstraintImpl.class }, implicitSuper = ConcreteInheritanceConstraintImpl.class)
	public static class DefaultValue {
	}

}
