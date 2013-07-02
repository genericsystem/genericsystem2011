package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AloneAutomaticsConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
public class AloneAutomaticsConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	public void check(Generic modified) throws ConstraintViolationException {
		if (modified.isAlive() && modified.isAutomatic() && modified.getInheritings().isEmpty() && modified.getComposites().isEmpty())
			throw new AloneAutomaticsConstraintViolationException();

	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}

}
