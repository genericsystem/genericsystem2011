package org.genericsystem.impl.constraints.simple;

import java.util.Arrays;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SuperRuleConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics.Primaries;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class SuperRuleConstraintImpl extends Constraint {

	private static final long serialVersionUID = 6874090673594299362L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		for (Generic directSuper : modified.getSupers()) {
			Generic[] interfaces = new Primaries(modified).toArray();
			Generic[] components = ((GenericImpl) modified).components;
			if (!GenericImpl.isSuperOf(((GenericImpl) directSuper).getPrimariesArray(), ((GenericImpl) directSuper).components, interfaces, components, true))
				throw new SuperRuleConstraintViolationException("Interfaces : " + Arrays.toString(interfaces) + " Components : " + Arrays.toString(components) + " should inherits from : " + directSuper);
		}
	}

}
