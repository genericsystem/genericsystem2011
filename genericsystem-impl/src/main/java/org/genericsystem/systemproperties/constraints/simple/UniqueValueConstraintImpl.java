package org.genericsystem.systemproperties.constraints.simple;

import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
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
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		if (modified.isStructural()) {
			final Generic primary = modified.getImplicit();
			Iterator<Generic> iterator = Statics.<Generic> valueFilter(((GenericImpl) primary).<Generic> directInheritingsIterator(), modified.getValue());
			if (!iterator.hasNext())
				return;
			iterator.next();
			if (iterator.hasNext() || !primary.isAutomatic())
				throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
		} else
			for (Generic generic : ((Type) type).getAllInstances())
				if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
					throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
	}
}
