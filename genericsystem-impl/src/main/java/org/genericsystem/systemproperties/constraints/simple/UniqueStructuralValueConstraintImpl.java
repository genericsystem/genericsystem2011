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
import org.genericsystem.iterator.AbstractFilterIterator;
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
@Dependencies(UniqueStructuralValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueStructuralValueConstraintImpl.class)
public class UniqueStructuralValueConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(UniqueStructuralValueConstraintImpl.class)
	@Extends(ConstraintsMapProvider.ConstraintValue.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		if (!modified.isStructural())
			return;
		final Generic primary = modified.getImplicit();
		Iterator<Generic> iterator = Statics.<Generic> valueFilter(new AbstractFilterIterator<Generic>((((GenericImpl) primary).<Generic> directInheritingsIterator())) {
			@Override
			public boolean isSelected() {
				return next.inheritsFrom(primary);
			}
		}, modified.getValue());
		if (!iterator.hasNext())
			return;
		iterator.next();
		if (iterator.hasNext() || !primary.isAutomatic())
			throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
	}
}
