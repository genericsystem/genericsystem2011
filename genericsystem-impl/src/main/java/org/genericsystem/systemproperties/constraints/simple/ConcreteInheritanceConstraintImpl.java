package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConcreteInheritanceConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
public class ConcreteInheritanceConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(modified, getClass()).isEmpty())
			if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
				if (((GenericImpl) modified).getSupers().get(0).isConcrete())
					throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified.info());
	}

}
