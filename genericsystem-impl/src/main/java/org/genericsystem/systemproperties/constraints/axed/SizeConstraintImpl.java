package org.genericsystem.systemproperties.constraints.axed;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SizeConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.systemproperties.constraints.Constraint;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl.Size;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies(Size.class)
public class SizeConstraintImpl extends Constraint {

	private static final long serialVersionUID = 6718716331173727864L;

	public static final String SIZE = "Size";

	@Override
	public void check(Cache cache, Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(cache, modified, getClass())) {
			// TODO KK because InstanceClassConstraint, see GenericImpl::setConstraintClass
			Serializable value = constraintValue.getValue();
			if (value instanceof Integer) {
				Integer axe = (Integer) value;
				final Generic component = ((Link) modified).getComponent(axe);
				Snapshot<Holder> holders = ((GenericImpl) component).getHolders(cache, (Relation) constraintValue.getConstraintBaseType(), axe);
				Integer size = ((Attribute) modified).getSizeConstraint(cache, axe);
				if (size != null && holders.size() > size)
					throw new SizeConstraintViolationException("Multiple links of type " + constraintValue.getConstraintBaseType() + ", and the maximum size is " + size);
			}
		}
	}

	@SystemGeneric
	@Components(SizeConstraintImpl.class)
	@StringValue(SIZE)
	@SingularConstraint
	public class Size {

	}

}
