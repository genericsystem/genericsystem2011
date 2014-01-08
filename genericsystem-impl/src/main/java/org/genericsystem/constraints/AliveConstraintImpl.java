package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AliveConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ AliveConstraintImpl.DefaultKey.class, AliveConstraintImpl.DefaultValue.class })
public class AliveConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = AliveConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(AliveConstraintImpl.class)
	public static class DefaultKey {}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		for (Generic generic : modified.components())
			if (!generic.isAlive())
				throw new AliveConstraintViolationException("Component : " + generic + " of added node " + modified + " should be alive.");
		for (Generic generic : modified.supers())
			if (!generic.isAlive())
				throw new AliveConstraintViolationException("Super : " + generic + " of added node " + modified + " should be alive.");
	}
}
