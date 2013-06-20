package org.genericsystem.systemproperties.constraints.simple;

import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
public class UniqueStructuralValueConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -7212219694902616927L;

	@Override
	public void check(Context context, Generic modified) throws ConstraintViolationException {
		if (!modified.isStructural())
			return;
		final Generic primary = modified.getImplicit();
		Iterator<Generic> iterator = Statics.<Generic> valueFilter(new AbstractFilterIterator<Generic>((((GenericImpl) primary).<Generic> directInheritingsIterator(context))) {
			@Override
			public boolean isSelected() {
				return next.inheritsFrom(primary);
			}
		}, modified.getValue());
		if (!iterator.hasNext())
			return;
		iterator.next();
		if (iterator.hasNext() || !primary.isAutomatic())
			throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());// + " direct : " + iterator.next().info());
	}
}
