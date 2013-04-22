package org.genericsystem.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.constraints.Constraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConcreteInheritanceConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.system.BooleanSystemProperty;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@NotNullConstraint
public class ConcreteInheritanceConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
				if (((GenericImpl) modified).getSupers().first().isConcrete())
					throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified.info());
	}

}
