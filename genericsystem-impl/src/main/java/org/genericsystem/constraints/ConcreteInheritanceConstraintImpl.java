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
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.map.ConstraintsMapProvider;
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
//@Dependencies(ConcreteInheritanceConstraintImpl.DefaultValue.class)
//@AxedConstraintValue(ConcreteInheritanceConstraintImpl.class)
//public class ConcreteInheritanceConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {
//
//	@SystemGeneric
//	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
//	@Components(ConcreteInheritanceConstraintImpl.class)
//	@BooleanValue(true)
//	public static class DefaultValue extends GenericImpl implements Holder {}
//
//	@Override
//	public void check(Generic modified, Generic type) throws ConstraintViolationException {
//		// if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
//		// if (((GenericImpl) modified).getSupers().get(0).isConcrete())
//		// throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified.info());
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
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
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
@Dependencies(ConcreteInheritanceConstraintImpl.DefaultValue.class)
@AxedConstraintValue(ConcreteInheritanceConstraintImpl.class)
public class ConcreteInheritanceConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(ConcreteInheritanceConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic constraintBase, Generic modified, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		// if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
		// if (((GenericImpl) modified).getSupers().get(0).isConcrete())
		// throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified.info());
	}
	// @Override
	// public void check(Generic base, Generic baseConstraint, int axe) throws ConstraintViolationException {
	// if (base.isConcrete() && ((GenericImpl) base).isPrimary())
	// if (((GenericImpl) base).getSupers().get(0).isConcrete())
	// throw new ConcreteInheritanceConstraintViolationException(base.getMeta() + " " + base.info());
	// }
}