package org.genericsystem.constraints;

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
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
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
public class SizeConstraintImpl extends AbstractNoBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(SizeConstraintImpl.class)
	@BooleanValue(false)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic base, Generic valueConstraint, int pos) throws ConstraintViolationException {
		// TODO KK because InstanceClassConstraint, see GenericImpl::setConstraintClass
		Serializable value = ((Holder) valueConstraint).getValue();
		Generic baseConstraint = ((Holder) valueConstraint).<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent();
		baseConstraint.log();
		Snapshot<Holder> holders = ((GenericImpl) base).getHolders((Relation) baseConstraint);
		if (value instanceof Integer)
			if (holders.size() > (Integer) value)
				throw new SizeConstraintViolationException("Multiple links of type " + baseConstraint + ", and the maximum size is " + value);
		if (baseConstraint.getComponentsSize() > 0 && ((Type) baseConstraint).getInstances().size() > (Integer) valueConstraint.getValue())
			throw new SizeConstraintViolationException("Multiple links of " + baseConstraint + ", and the maximum size is " + valueConstraint);
	}

}
