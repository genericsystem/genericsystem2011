package org.genericsystem.impl.constraints.simple;

import java.util.Arrays;
import java.util.Iterator;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.DuplicateStructuralValueConstraintViolationException;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class DuplicateStructuralValueConstraintImpl extends Constraint {

	private static final long serialVersionUID = -7212219694902616927L;

	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
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
