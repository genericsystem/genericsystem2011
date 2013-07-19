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
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(ConstraintKey.class)
@SingularConstraint
@Dependencies(AliveConstraintImpl.DefaultValue.class)
@AxedConstraintValue(AliveConstraintImpl.class)
public class AliveConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(AliveConstraintImpl.class)
	@Extends(ConstraintsMapProvider.ConstraintValue.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(final Generic modified, final Generic baseComponent) throws ConstraintViolationException {
		for (Generic generic : ((GenericImpl) modified).getComponents())
			if (!generic.isAlive())
				throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");
		for (Generic generic : ((GenericImpl) modified).getSupers())
			if (!generic.isAlive())
				throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
	}

}
