package org.genericsystem.constraints.axed;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
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

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@NotNullConstraint
public class SingularConstraintImpl extends AbstractAxedIntegerConstraint {

	private static final long serialVersionUID = 8068202813624343936L;

	// TODO do the same as PropertyConstraint

	@Override
	protected void internalCheck(final Context context, Generic modified, final Relation constraintType, final Integer basePos) throws ConstraintViolationException {
		final Generic component = ((Link) modified).getComponent(basePos);
		Snapshot<Holder> holders = ((GenericImpl) component).getHolders(context, constraintType, basePos);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of type " + constraintType + " on target " + component + " (n° " + basePos + ") : " + holders);
	}

}
