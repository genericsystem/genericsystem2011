package org.genericsystem.impl.constraints.simple;

import java.util.Iterator;
import java.util.Objects;

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
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.constraints.Constraint;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.system.ComponentPosValue;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
@InstanceClassConstraint(ComponentPosValue.class)
@NotNullConstraint
public class PropertyConstraintImpl extends Constraint {

	private static final long serialVersionUID = -6429972259714036057L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		Snapshot<Value> constraintInstances = getConstraintValues(context, modified, getClass());
		for (Value constraintValueNode : constraintInstances) {
			Type constraintBaseType = constraintValueNode.<Type> getBaseComponent();
			if (modified.isAttribute()) {
				for (final Generic inheriting : ((GenericImpl) ((Value) modified).getBaseComponent()).getAllInheritings(context)) {
					Iterator<Generic> it = new AbstractFilterIterator<Generic>((Iterator) inheriting.getValueHolders(context, (Attribute) constraintBaseType).iterator()) {
						@Override
						public boolean isSelected() {
							for (int componentPos = 1; componentPos < next.getComponents().size(); componentPos++)
								if (!Objects.equals(((Value) next).getComponent(componentPos), ((Value) modified).getComponent(componentPos)))
									return false;
							return true;
						}
					};
					if (it.hasNext()) {
						Generic value = it.next();
						if (it.hasNext())
							throw new PropertyConstraintViolationException(value.info() + it.next().info());
					}
				}
				return;
			}
			if (new AbstractFilterIterator<Generic>(constraintBaseType.getAllInstances(context).iterator()) {
				@Override
				public boolean isSelected() {
					return !next.equals(modified) && Objects.equals(next.getValue(), modified.getValue());
				}
			}.hasNext())
				throw new PropertyConstraintViolationException("");
		}
	}

}
