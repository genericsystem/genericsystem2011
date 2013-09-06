package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SizeConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@Dependencies(SizeConstraintImpl.DefaultValue.class)
@AxedConstraintValue(SizeConstraintImpl.class)
public class SizeConstraintImpl extends AbstractNoBooleanAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(SizeConstraintImpl.class)
	@BooleanValue(false)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic base, Generic attribute, int pos, Serializable value) throws ConstraintViolationException {
		// TODO KK because InstanceClassConstraint, see GenericImpl::setConstraintClass
		Snapshot<Holder> holders = ((GenericImpl) base).getHolders((Relation) attribute);
		if (value instanceof Integer)
			if (holders.size() > (Integer) value)
				throw new SizeConstraintViolationException("Multiple links of type " + attribute + ", and the maximum size is " + value);
	}

}
