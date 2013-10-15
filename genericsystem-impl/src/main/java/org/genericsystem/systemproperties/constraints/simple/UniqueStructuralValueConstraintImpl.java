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
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
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
@Dependencies(UniqueStructuralValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueStructuralValueConstraintImpl.class)
public class UniqueStructuralValueConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(UniqueStructuralValueConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		int modifiedComponentsSize = modified.getComponentsSize();
		if (!modified.isStructural() || modifiedComponentsSize == 0)
			return;
		for (Generic superComponent : modified.getSupers()) {
			int superComponentsSize = superComponent.getComponentsSize();
			if (superComponent.fastValueEquals(modified) && superComponentsSize != modifiedComponentsSize)
				throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
		}
	}
}
