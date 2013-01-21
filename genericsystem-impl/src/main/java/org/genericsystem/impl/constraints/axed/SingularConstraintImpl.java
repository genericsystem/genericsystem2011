package org.genericsystem.impl.constraints.axed;

import java.util.Iterator;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.impl.core.GenericImpl;

@SystemGeneric
@Components(Engine.class)
@InstanceClassConstraint(Integer.class)
public class SingularConstraintImpl extends AbstractAxedIntegerConstraint {

	private static final long serialVersionUID = 8068202813624343936L;

	// TODO do the same as PropertyConstraint

	@Override
	// TODO clean comment
	protected void internalCheck(final Context context, Generic modified, final Relation constraintType, final Integer basePos) throws ConstraintViolationException {
		final Generic component = ((Link) modified).getComponent(basePos);
		// if (component.getLinks(context, constraintType, basePos).size() > 1)
		// throw new SingularConstraintViolationException("Multiple links of type " + constraintType + " on target " + component + " (n° " + basePos + ").");
		Iterator<Generic> it = ((GenericImpl) component).<Generic> mainIterator(context, constraintType, SystemGeneric.CONCRETE, basePos/* , false */);
		if (it.hasNext()) {
			it.next();
			if (it.hasNext())
				throw new SingularConstraintViolationException("Multiple links of type " + constraintType + " on target " + component + " (n° " + basePos + ").");
		}

	}

}
