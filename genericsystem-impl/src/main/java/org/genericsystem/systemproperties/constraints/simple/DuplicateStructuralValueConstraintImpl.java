package org.genericsystem.systemproperties.constraints.simple;

import java.util.Arrays;
import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.NotNullConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.DuplicateStructuralValueConstraintViolationException;
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
@NotNullConstraint
public class DuplicateStructuralValueConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -7212219694902616927L;

	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		if (!getConstraintValues(context, modified, getClass()).isEmpty())
			if (modified.isAttribute() && modified.isStructural()) {
				Snapshot<Generic> components = modified.getComponents();
				final Generic[] primariesArray = ((GenericImpl) modified).getPrimariesArray();
				for (int i = 0; i < components.size(); i++) {
					Generic component = components.get(i);
					Iterator<Generic> filterIterator = new AbstractFilterIterator<Generic>(((GenericImpl) component).compositesIterator(context, i)) {
						@Override
						public boolean isSelected() {
							return !modified.equals(next) && Arrays.equals(primariesArray, ((GenericImpl) next).getPrimariesArray());
						}
					};
					if (filterIterator.hasNext())
						throw new DuplicateStructuralValueConstraintViolationException("modified : " + modified.info() + "component : " + component.info() + " composite : " + filterIterator.next().info());
				}
			}
	}
}
