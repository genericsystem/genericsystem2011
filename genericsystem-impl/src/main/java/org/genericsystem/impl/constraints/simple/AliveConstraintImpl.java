package org.genericsystem.impl.constraints.simple;

import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Interfaces;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@Dependencies(AliveConstraintImpl.DefaultValue.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class AliveConstraintImpl extends AbstractSimpleBooleanConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		for (Generic generic : ((GenericImpl) modified).getComponents())
			if (generic != null && !generic.isAlive(context))
				throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");
		
		for (Generic generic : ((GenericImpl) modified).getSupers())
			if (!generic.isAlive(context))
				throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
		
	}
	
	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	@BooleanValue(true)
	@Interfaces(AliveConstraintImpl.class)
	public static class DefaultValue {}
	
}
