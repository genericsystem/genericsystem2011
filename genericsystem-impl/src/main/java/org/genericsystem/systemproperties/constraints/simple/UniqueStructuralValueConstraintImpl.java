package org.genericsystem.systemproperties.constraints.simple;

import java.util.Iterator;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
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
	public static class DefaultValue extends GenericImpl implements Holder {}

	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		if (!modified.isStructural())
			return;
		Iterator<Generic> iterator = Statics.<Generic> valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue());
		if (!iterator.hasNext())
			return;
		iterator.next();
		if (iterator.hasNext() /* || !primary.isAutomatic() */)
			throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
	}
}
