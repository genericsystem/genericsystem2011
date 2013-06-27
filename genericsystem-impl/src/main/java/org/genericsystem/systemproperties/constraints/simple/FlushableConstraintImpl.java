// TODO clean
//package org.genericsystem.systemproperties.constraints.simple;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.constraints.SingularConstraint;
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.Engine;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.Transaction;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.systemproperties.BooleanSystemProperty;
//import org.genericsystem.systemproperties.constraints.Constraint;
//
///**
// * @author Nicolas Feybesse
// * 
// */
//@SystemGeneric(defaultBehavior = true)
//@Components(Engine.class)
//@SingularConstraint
//public class FlushableConstraintImpl extends Constraint implements BooleanSystemProperty {
//
//	private static final long serialVersionUID = -6429972259714036057L;
//
//	@Override
//	public void check(Cache cache, Generic modified) throws ConstraintViolationException {
//		if (context instanceof Transaction)
//			if (!((Transaction) context).isFlushable(modified))
//				assert false;
//		// TODO implements !?
//		// throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
//	}
//
//	@Override
//	public boolean isImmediatelyCheckable() {
//		return false;
//	}
//
// }
