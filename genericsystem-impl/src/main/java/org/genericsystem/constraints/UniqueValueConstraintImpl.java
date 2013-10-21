package org.genericsystem.constraints;

import java.util.Arrays;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * 
 * 
 * @author Nicolas Feybesse
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
//@Dependencies(UniqueValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	//@SystemGeneric
	//@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	//@Components(UniqueValueConstraintImpl.class)
	//@BooleanValue(true)
	//public static class DefaultValue extends GenericImpl implements Holder {
	//}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type, int axe) throws ConstraintViolationException {
		if (!modified.isStructural())
			for (Generic generic : (((Type) type).getAllInstances())) {
				if (!generic.equals(modified) &&
						generic.getValue().equals(modified.getValue()) &&
						Arrays.equals(((GenericImpl) generic).getComponentsArray(), ((GenericImpl) modified).getComponentsArray()))
					throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
			}
	}

}
