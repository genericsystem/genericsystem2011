package org.genericsystem.impl.constraints.simple;

import java.util.Arrays;

import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Interfaces;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SuperRuleConstraintViolationException;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics.Primaries;

@SystemGeneric
@Components(Engine.class)
@Dependencies(SuperRuleConstraintImpl.DefaultValue.class)
@PropertyConstraint
public class SuperRuleConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		for (Generic directSuper : modified.getSupers()) {
			Generic[] interfaces = new Primaries(modified).toArray();
			Generic[] components = ((GenericImpl) modified).components;
			if (!GenericImpl.isSuperOf2(((GenericImpl) directSuper).getPrimariesArray(), ((GenericImpl) directSuper).components, interfaces, components))
				throw new SuperRuleConstraintViolationException("Interfaces : " + Arrays.toString(interfaces) + " Components : " + Arrays.toString(components) + " should inherits from : " + directSuper);
		}
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	@BooleanValue(true)
	@Interfaces(SuperRuleConstraintImpl.class)
	public static class DefaultValue {
	}

}
