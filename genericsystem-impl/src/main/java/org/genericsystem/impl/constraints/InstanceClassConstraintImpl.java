package org.genericsystem.impl.constraints;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint(Statics.BASE_POSITION)
// @PropertyConstraint
public class InstanceClassConstraintImpl extends AbstractConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		for (Value constraintValueNode : ((GenericImpl) modified).getConstraintInstances(context, this.getClass()))
			if (SystemGeneric.CONCRETE == modified.getMetaLevel() && ((GenericImpl) modified).getSystemPropertyValue(context, this.getClass()) != null) {
				Class<?> clazz = constraintValueNode.getValue();
				if (!clazz.isAssignableFrom(modified.getValue().getClass()))
					throw new ClassInstanceConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is "
							+ modified.getValue().getClass().getSimpleName() + " for type " + constraintValueNode.getBaseComponent().getValue());
			}
	}
	
}
