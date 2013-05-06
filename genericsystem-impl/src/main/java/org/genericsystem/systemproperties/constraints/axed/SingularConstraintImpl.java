package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Priority(5)
public class SingularConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = 8068202813624343936L;

	// TODO do the same as PropertyConstraint

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass())) {
			// TODO KK because InstanceClassConstraint, see GenericImpl::setConstraintClass
			Serializable value = constraintValue.getValue();
			if (value instanceof Integer) {
				Integer axe = (Integer) value;
				final Generic component = ((Link) modified).getComponent(axe);
				Snapshot<Holder> holders = ((GenericImpl) component).getHolders(context, (Relation) constraintValue.getConstraintType(), axe);
				if (holders.size() > 1)
					throw new SingularConstraintViolationException("Multiple links of type " + constraintValue.getConstraintType() + " on target " + component + " (n° " + axe + ") : " + holders);
			}
		}
	}

}
