package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AbstractConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.impl.system.ComponentPosValue;

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
