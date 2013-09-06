package org.genericsystem.systemproperties.constraints.simple;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConcreteInheritanceConstraintViolationException;
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
public class ConcreteInheritanceConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(ConcreteInheritanceConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		if (modified.isConcrete() && ((GenericImpl) modified).isPrimary())
			if (((GenericImpl) modified).getSupers().get(0).isConcrete())
				throw new ConcreteInheritanceConstraintViolationException(modified.getMeta() + " " + modified.info());
	}

}
