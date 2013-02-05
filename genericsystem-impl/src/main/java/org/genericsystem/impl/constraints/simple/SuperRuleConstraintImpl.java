package org.genericsystem.impl.constraints.simple;

import java.util.Arrays;

import org.genericsystem.api.annotation.BooleanValue;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
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
@SingularConstraint
public class SuperRuleConstraintImpl extends AbstractSimpleBooleanConstraint {

	private static final long serialVersionUID = 6874090673594299362L;

	@Override
	protected void internalCheck(Context context, Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		for (Generic directSuper : modified.getSupers()) {
			Generic[] interfaces = new Primaries(modified).toArray();
			Generic[] components = ((GenericImpl) modified).components;
			if (!GenericImpl.isSuperOf(((GenericImpl) directSuper).getPrimariesArray(), ((GenericImpl) directSuper).components, interfaces, components, true))
				throw new SuperRuleConstraintViolationException("Interfaces : " + Arrays.toString(interfaces) + " Components : " + Arrays.toString(components) + " should inherits from : " + directSuper);
		}
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(Engine.class)
	@BooleanValue(true)
	@Supers(value = { SuperRuleConstraintImpl.class }, implicitSuper = SuperRuleConstraintImpl.class)
	public static class DefaultValue {
	}

}
