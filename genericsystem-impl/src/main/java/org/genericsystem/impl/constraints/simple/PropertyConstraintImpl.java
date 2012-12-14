package org.genericsystem.impl.constraints.simple;

import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;
import org.genericsystem.impl.iterator.AbstractFilterIterator;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint(Statics.BASE_POSITION)
@InstanceClassConstraint(Boolean.class)
public class PropertyConstraintImpl extends AbstractSimpleBooleanConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void internalCheck(Context context, final Generic modified, Generic constraintBaseType) throws ConstraintViolationException {
		if (modified.isAttribute()) {
//			for (final Generic baseComponent : ((GenericImpl) ((Value) modified).getBaseComponent()).getAllInheritings(context)) {
				Iterator<Generic> it = new AbstractFilterIterator<Generic>((Iterator) constraintBaseType.getValues(context, (Attribute) constraintBaseType).iterator()) {
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
//			}
			return;
		}
		if (new AbstractFilterIterator<Generic>(((Type) constraintBaseType).getAllInstances(context).iterator()) {
			@Override
			public boolean isSelected() {
				return !next.equals(modified) && Objects.equals(next.getValue(), modified.getValue());
			}
		}.hasNext())
			throw new PropertyConstraintViolationException("");
	}
	
}
