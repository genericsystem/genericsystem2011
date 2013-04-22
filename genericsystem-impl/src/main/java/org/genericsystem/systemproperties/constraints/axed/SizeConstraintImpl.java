package org.genericsystem.systemproperties.constraints.axed;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.systemproperties.constraints.Constraint;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl.Size;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@NotNullConstraint
@Dependencies(Size.class)
public class SizeConstraintImpl extends Constraint {

	private static final long serialVersionUID = 6718716331173727864L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		// TODO implements

		// for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass())) {
		// Serializable value = constraintValue.getValue();
		// if (value instanceof Integer) {
		// Integer axe = (Integer) value;
		// final Generic component = ((Link) modified).getComponent(axe);
		// Snapshot<Holder> holders = ((GenericImpl) component).getHolders(context, (Attribute) constraintValue.getConstraintType(), axe);
		// if (holders.size() == 3)
		// throw new SingularConstraintViolationException("Multiple links of type " + constraintValue.getConstraintType() + " on target " + component + " (n° " + axe + ") : " + holders);
		// }
		// }
	}

	@SystemGeneric
	@Components(SizeConstraintImpl.class)
	@StringValue("Size")
	public class Size {

	}

}
