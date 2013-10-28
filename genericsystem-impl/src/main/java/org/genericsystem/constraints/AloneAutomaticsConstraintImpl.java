//package org.genericsystem.systemproperties.constraints;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.Dependencies;
//import org.genericsystem.annotation.Extends;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.constraints.SingularConstraint;
//import org.genericsystem.annotation.value.AxedConstraintValue;
//import org.genericsystem.annotation.value.BooleanValue;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.GenericImpl;
//import org.genericsystem.exception.AloneAutomaticsConstraintViolationException;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.map.ConstraintsMapProvider;
//import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
//import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
//
///**
// * @author Nicolas Feybesse
// * @author Michael Ory
// */
//@SystemGeneric
//@Extends(meta = ConstraintKey.class)
//@Components(MapInstance.class)
//@SingularConstraint
//@Dependencies(AloneAutomaticsConstraintImpl.DefaultValue.class)
//@AxedConstraintValue(AloneAutomaticsConstraintImpl.class)
//public class AloneAutomaticsConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {
//
//	@SystemGeneric
//	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
//	@Components(AloneAutomaticsConstraintImpl.class)
//	@BooleanValue(true)
//	public static class DefaultValue extends GenericImpl implements Holder {}
//
//	@Override
//	public void check(Generic modified, Generic type) throws ConstraintViolationException {
//		if (modified.isAlive() /* && modified.isAutomatic() */&& modified.getInheritings().isEmpty() && modified.getComposites().isEmpty())
//			throw new AloneAutomaticsConstraintViolationException();
//
//	}
//
//	@Override
//	public boolean isCheckedAt(Generic modified, CheckingType type) {
//		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
//	}
//
//}
package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.AloneAutomaticsConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(AloneAutomaticsConstraintImpl.DefaultValue.class)
@AxedConstraintValue(AloneAutomaticsConstraintImpl.class)
public class AloneAutomaticsConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(AloneAutomaticsConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		if (constraintBase.isAlive() /* && modified.isAutomatic() */&& constraintBase.getInheritings().isEmpty() && constraintBase.getComposites().isEmpty())
			throw new AloneAutomaticsConstraintViolationException();
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}
}
