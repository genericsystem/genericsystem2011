package org.genericsystem.constraints.axed;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class SingularConstraintImpl extends AbstractAxedIntegerConstraint {

	private static final long serialVersionUID = 8068202813624343936L;

	// TODO do the same as PropertyConstraint

	@Override
	protected void internalCheck(final Context context, Generic modified, final Relation constraintType, final Integer basePos) throws AbstractConstraintViolationException {
		final Generic component = ((Link) modified).getComponent(basePos);
		Snapshot<Holder> holders = component.getHolders(context, constraintType, basePos);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of type " + constraintType + " on target " + component + " (n° " + basePos + ") : " + holders);
	}

}
