//TODO KK
//package org.genericsystem.systemproperties.constraints.simple;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.Dependencies;
//import org.genericsystem.annotation.Extends;
//import org.genericsystem.annotation.Priority;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.constraints.SingularConstraint;
//import org.genericsystem.annotation.value.AxedConstraintValue;
//import org.genericsystem.annotation.value.BooleanValue;
//import org.genericsystem.core.EngineImpl;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.GenericImpl;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.exception.EngineConsistencyConstraintViolationException;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.map.ConstraintsMapProvider;
//import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
//import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
//
///**
// * @author Nicolas Feybesse
// * 
// */
//@SystemGeneric(Statics.CONCRETE)
//@Components(MapInstance.class)
//@Extends(SystemPropertyKey.class)
//@SingularConstraint
//@Dependencies(EngineConsistencyConstraintImpl.DefaultValue.class)
//@AxedConstraintValue(EngineConsistencyConstraintImpl.class)
//@Priority(Integer.MIN_VALUE)
//public class EngineConsistencyConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {
//
//	@SystemGeneric(Statics.CONCRETE)
//	@Components(EngineConsistencyConstraintImpl.class)
//	@Extends(ConstraintsMapProvider.ConstraintValue.class)
//	@BooleanValue(true)
//	public static class DefaultValue extends GenericImpl implements Holder {
//	}
//
//	@Override
//	public void check(final Generic modified, final Generic baseComponent) throws ConstraintViolationException {
//		if (!modified.getEngine().equals(((EngineImpl) modified.getEngine()).getCurrentCache().getEngine()))
//			throw new EngineConsistencyConstraintViolationException("The Engine of " + modified + " isn't equals at Engine of the Context");
//	}
// }
