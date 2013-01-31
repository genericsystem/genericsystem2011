package org.genericsystem.impl.constraints.axed;

import java.util.Iterator;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
public class RequiredAxedConstraintImpl extends Constraint {

	private static final long serialVersionUID = 2837810754525623146L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (modified.isConcrete()) {
			Iterator<Generic> requiredRelationIterator = ((Type) modified.getMeta()).getComposites(context).iterator();
			while (requiredRelationIterator.hasNext())
				checkRequiredRelation(modified, (Relation) requiredRelationIterator.next(), context);
		}
	}

	private void checkRequiredRelation(Generic modified, Relation requiredRelation, Context context) throws ConstraintViolationException {
		for (Value constraintValueNode : getConstraintInstances(context, requiredRelation, RequiredAxedConstraintImpl.class)) {
			if (!(constraintValueNode.getValue() instanceof Integer))
				throw new ConstraintViolationException("The constraint " + RequiredAxedConstraintImpl.class + " must be axed");
			Integer componentPos = constraintValueNode.getValue();
			if (componentPos == null)
				throw new ConstraintViolationException("The constraint " + RequiredAxedConstraintImpl.class + " must have a not null value");
			Generic component = requiredRelation.getComponent(componentPos);
			if (!((Type) component).getAllInstances(context).isEmpty() && component.getLinks(context, requiredRelation, componentPos).size() < 1)
				throw new RequiredConstraintViolationException(requiredRelation.getValue() + " is required for new " + modified.getMeta() + " " + modified);
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
