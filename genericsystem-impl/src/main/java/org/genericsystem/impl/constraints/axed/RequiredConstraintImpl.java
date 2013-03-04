package org.genericsystem.impl.constraints.axed;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.AbstractConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class RequiredConstraintImpl extends Constraint {

	private static final long serialVersionUID = 2837810754525623146L;

	@Override
	public void check(Context context, Generic modified) throws AbstractConstraintViolationException {
		if (modified.isConcrete()) {
			if (!modified.isAlive(context)) {
				for (ConstraintValue constraintValue : getConstraintValues(context, modified, RequiredConstraintImpl.class)) {
					int componentPos = constraintValue.getComponentPosValue().getComponentPos();
					Generic base = ((Attribute) modified).getComponent(componentPos);
					if (base != null && base.getLinks(context, modified.<Relation> getMeta(), componentPos).size() < 1)
						throw new RequiredConstraintViolationException(modified.getMeta().getValue() + " is required for " + base.getMeta() + " " + base);
				}
			} else
				for (Attribute requiredAttribute : ((Type) modified).getAttributes(context)) {
					for (ConstraintValue constraintValue : getConstraintValues(context, requiredAttribute, RequiredConstraintImpl.class))
						if (modified.inheritsFrom(requiredAttribute.<Type> getComponent(constraintValue.getComponentPosValue().getComponentPos())) && modified.getHolders(context, requiredAttribute).size() < 1)
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
