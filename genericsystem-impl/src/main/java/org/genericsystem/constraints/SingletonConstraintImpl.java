//package org.genericsystem.systemproperties.constraints;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.Extends;
//import org.genericsystem.annotation.Priority;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.constraints.SingularConstraint;
//import org.genericsystem.annotation.value.AxedConstraintValue;
//import org.genericsystem.core.Generic;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.exception.SingletonConstraintViolationException;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.generic.Type;
//import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
//import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
//
///**
// * @author Nicolas Feybesse
// * 
// */
//@SystemGeneric
//@Extends(meta = ConstraintKey.class)
//@Components(MapInstance.class)
//@SingularConstraint
//@AxedConstraintValue(SingletonConstraintImpl.class)
//@Priority(10)
//public class SingletonConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {
//
//	@Override
//	public void check(Generic modified, Generic type) throws ConstraintViolationException {
//		int instanceNumber = ((Type) type).getAllInstances().size();
//		if (instanceNumber > 1)
//			throw new SingletonConstraintViolationException("Singular instance constraint violation : type " + type + " has " + instanceNumber + " instances.");
//	}
//
//}
package org.genericsystem.constraints;


import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingletonConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(SingletonConstraintImpl.class)
@Priority(10)
public class SingletonConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic baseConstraint, int axe) throws ConstraintViolationException {
		int instanceNumber = ((Type)baseConstraint ).getAllInstances().size();
		if (instanceNumber > 1)
			throw new SingletonConstraintViolationException("Singleton constraint violation : type " + baseConstraint + " has " + instanceNumber + " instances.");
	}

	@Override
	public void checkConsistency(Generic baseConstraint,Holder valueConstraint, int axe) throws ConstraintViolationException {
		int instanceNumber = ((Type) baseConstraint).getAllInstances().size();
		if (instanceNumber > 1)
			throw new SingletonConstraintViolationException("Singleton constraint violation : type " + baseConstraint + " has " + instanceNumber + " instances.");
	}


}