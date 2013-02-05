package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@InstanceClassConstraint(ComponentPosValue.class)
public class RequiredAxedConstraintImpl extends Constraint {

	private static final long serialVersionUID = 2837810754525623146L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (modified.isConcrete()) {
			if (!modified.isAlive(context)) {
				for (Value constraintValueNode : getConstraintValues(context, modified, RequiredAxedConstraintImpl.class)) {
					int componentPos = constraintValueNode.<ComponentPosValue<Boolean>> getValue().getComponentPos();
					Generic base = ((Attribute) modified).getComponent(componentPos);
					if (base != null && base.getLinks(context, modified.<Relation> getMeta(), componentPos).size() < 1)
						throw new RequiredConstraintViolationException(modified.getMeta().getValue() + " is required for " + base.getMeta() + " " + base);
				}
			} else
				for (Attribute requiredAttribute : ((Type) modified).getAttributes(context)) {
					for (Value constraintValueNode : getConstraintValues(context, requiredAttribute, RequiredAxedConstraintImpl.class))
						if (modified.inheritsFrom(requiredAttribute.<Type> getComponent(constraintValueNode.<ComponentPosValue<Boolean>> getValue().getComponentPos())) && modified.getLinks(context, (Relation) requiredAttribute).size() < 1)
							throw new RequiredConstraintViolationException(requiredAttribute.getValue() + " is required for new " + modified.getMeta() + " " + modified);
				}
		}
	}

	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE) || type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

}
