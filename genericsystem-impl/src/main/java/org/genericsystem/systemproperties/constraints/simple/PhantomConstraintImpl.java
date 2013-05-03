package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PhantomConstraintViolationException;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
// @NotNullConstraint
public class PhantomConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -1175582355395269087L;

	// TODO KK
	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		// if (!getConstraintValues(context, modified, getClass()).isEmpty())
		Generic[] supers = ((GenericImpl) modified).getSupersArray();
		if (modified.getValue() == null)
			if (modified.getComponentsSize() != 0) {
				if (supers.length != 2 || (modified.isStructural() ? supers[1].isConcrete() : supers[1].isStructural()))
					throw new PhantomConstraintViolationException(modified.info());

				Generic[] components = ((GenericImpl) supers[1]).getComponentsArray();
				Generic[] subComponents = ((GenericImpl) modified).getComponentsArray();
				assert components.length >= subComponents.length;

				int inheritancesCount = 0;
				for (int i = 0; i < subComponents.length; i++) {
					Generic component = components[i];
					Generic subComponent = subComponents[i];
					if (component != subComponent) {
						if (!subComponent.inheritsFrom(component))
							throw new PhantomConstraintViolationException(modified.info() + subComponent.info() + component.info());
						inheritancesCount++;
					}
				}
				if (inheritancesCount != 1)
					throw new PhantomConstraintViolationException(modified.info());
			}
	}
}
