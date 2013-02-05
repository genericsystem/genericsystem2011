//package org.genericsystem.impl.constraints;
//
//import org.genericsystem.api.annotation.Components;
//import org.genericsystem.api.annotation.SystemGeneric;
//import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
//import org.genericsystem.api.annotation.constraints.NotNullConstraint;
//import org.genericsystem.api.annotation.constraints.SingularConstraint;
//import org.genericsystem.api.core.Context;
//import org.genericsystem.api.core.Engine;
//import org.genericsystem.api.core.Generic;
//import org.genericsystem.api.exception.ConstraintViolationException;
//import org.genericsystem.api.exception.RequiredConstraintViolationException;
//import org.genericsystem.api.generic.Attribute;
//import org.genericsystem.api.generic.Type;
//import org.genericsystem.api.generic.Value;
//import org.genericsystem.impl.core.GenericImpl;
//
//@SystemGeneric
//@Components(Engine.class)
//@SingularConstraint
//@InstanceClassConstraint(Boolean.class)
//@NotNullConstraint
//public class RequiredConstraintImpl extends Constraint {
//
//	private static final long serialVersionUID = -6429972259714036057L;
//
//	@Override
//	public void check(Context context, final Generic modified) throws ConstraintViolationException {
//		if (!modified.isAlive(context))
//			checkRequired(((Value) modified).getBaseComponent(), ((Value) modified).getMeta(), context);
//		else if (SystemGeneric.CONCRETE == modified.getMetaLevel())
//			for (Attribute requiredAttribute : ((Type) modified.getMeta()).getAttributes(context))
//				checkRequired(modified, requiredAttribute, context);
//	}
//
//	private void checkRequired(Generic baseGeneric, Generic requiredGeneric, Context context) throws RequiredConstraintViolationException {
//		if (((GenericImpl) requiredGeneric).isSystemPropertyEnabled(context, RequiredConstraintImpl.class) && baseGeneric.getValueHolders(context, (Attribute) requiredGeneric).isEmpty())
//			throw new RequiredConstraintViolationException("The generic " + baseGeneric + " has no value for the attribute " + requiredGeneric + ".");
//	}
//
//	@Override
//	public boolean isCheckedAt(CheckingType type) {
//		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE) || type.equals(CheckingType.CHECK_ON_ADD_NODE);
//	}
//
//	@Override
//	public boolean isImmediatelyCheckable() {
//		return false;
//	}
//
// }
