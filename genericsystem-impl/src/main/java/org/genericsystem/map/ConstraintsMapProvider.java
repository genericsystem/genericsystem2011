package org.genericsystem.map;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.AbstractSimpleConstraintImpl;
import org.genericsystem.systemproperties.constraints.Constraint.CheckingType;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ SingularConstraintImpl.class })
public class ConstraintsMapProvider extends AbstractMapProvider<Serializable, Boolean> {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getKeyAttributeClass() {
		return (Class<T>) ConstraintKey.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) ConstraintValue.class;
	}

	@SystemGeneric
	@Components(ConstraintsMapProvider.class)
	public static class ConstraintKey extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Components(ConstraintKey.class)
	@SingularConstraint
	@RequiredConstraint
	public static class ConstraintValue extends GenericImpl implements Attribute {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Extends(ConstraintsMapProvider.class)
	@Components(Engine.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(MapInstance.class)
	@Extends(ConstraintKey.class)
	@SingularConstraint
	@AxedConstraintValue(PropertyConstraintImpl.class)
	public static class PropertyConstraintImpl extends AbstractSimpleConstraintImpl implements Holder, BooleanSystemProperty {

		@Override
		public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
			return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
		}

		@Override
		public void check(final Generic baseComponent, final Generic modified) throws ConstraintViolationException {
			if (modified.isAttribute()) {
				// TODO KK
				for (final Generic inheriting : ((GenericImpl) ((Holder) modified).getBaseComponent()).getAllInheritings()) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					Iterator<Generic> it = new AbstractFilterIterator<Generic>((Iterator) inheriting.getHolders((Attribute) baseComponent).iterator()) {
						@Override
						public boolean isSelected() {
							for (int componentPos = 1; componentPos < next.getComponents().size(); componentPos++)
								if (!Objects.equals(((Holder) next).getComponent(componentPos), ((Holder) baseComponent).getComponent(componentPos)))
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
			if (new AbstractFilterIterator<Generic>(((GenericImpl) baseComponent).getAllInstances().iterator()) {
				@Override
				public boolean isSelected() {
					return !next.equals(modified) && Objects.equals(next.getValue(), modified.getValue());
				}
			}.hasNext())
				throw new PropertyConstraintViolationException("");
		}

	}

}
