package org.genericsystem.systemproperties.constraints.simple;

import java.util.Iterator;
import java.util.Objects;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Context;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.Constraint;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@SingularConstraint
public class PropertyConstraintImpl extends Constraint implements BooleanSystemProperty {

	private static final long serialVersionUID = -6429972259714036057L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		for (ConstraintValue constraintValue : getConstraintValues(context, modified, getClass())) {
			Type constraintBaseType = (Type) constraintValue.getConstraintType();
			if (modified.isAttribute()) {
				// TODO KK
				for (final Generic inheriting : ((GenericImpl) ((Holder) modified).getBaseComponent()).getAllInheritings(context)) {
					Iterator<Generic> it = new AbstractFilterIterator<Generic>((Iterator) inheriting.getHolders(context, (Attribute) constraintBaseType).iterator()) {
						@Override
						public boolean isSelected() {
							for (int componentPos = 1; componentPos < next.getComponents().size(); componentPos++)
								if (!Objects.equals(((Holder) next).getComponent(componentPos), ((Holder) modified).getComponent(componentPos)))
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
