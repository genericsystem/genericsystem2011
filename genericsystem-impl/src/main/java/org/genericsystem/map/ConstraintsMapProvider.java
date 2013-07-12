package org.genericsystem.map;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider.SingularConstraintImpl;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.AbstractAxedConstraintImpl;

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
	@Dependencies(SingularConstraintImpl.DefaultValue.class)
	// TODO KK Singular is axed
	public static class SingularConstraintImpl extends AbstractAxedConstraintImpl implements Holder, BooleanSystemProperty {

		private static final long serialVersionUID = 5155132185576732814L;

		@SystemGeneric(SystemGeneric.CONCRETE)
		@Components(SingularConstraintImpl.class)
		@Extends(ConstraintsMapProvider.ConstraintValue.class)
		@BooleanValue(false)
		public static class DefaultValue extends GenericImpl implements Holder {
		}

		@Override
		public void check(Generic modified) throws ConstraintViolationException {
			assert false;
			// for (ConstraintValue constraintValue : getConstraintValues(modified, getClass())) {
			// // TODO KK because InstanceClassConstraint, see GenericImpl::setConstraintClass
			// Serializable value = constraintValue.getValue();
			// if (value instanceof Integer) {
			// Integer axe = (Integer) value;
			// final Generic component = ((Link) modified).getComponent(axe);
			// Snapshot<Holder> holders = ((GenericImpl) component).getHolders((Relation) constraintValue.getConstraintBaseType(), axe);
			// if (holders.size() > 1)
			// throw new SingularConstraintViolationException("Multiple links of type " + constraintValue.getConstraintBaseType() + " on target " + component + " (n° " + axe + ") : " + holders);
			// }
			// }
		}

		@Override
		public void check(Generic baseComponent, Generic modified, int axe) throws ConstraintViolationException {
			Generic component = ((Link) modified).getComponent(axe);

			final Generic constraintValue = ((GenericImpl) modified).getCurrentCache().find(ConstraintsMapProvider.ConstraintValue.class);
			Iterator<Holder> filterIterator = new AbstractFilterIterator<Holder>(holdersIterator((Attribute) baseComponent, axe, false, new Generic[] {})) {
				@Override
				public boolean isSelected() {
					return next.inheritsFrom(constraintValue);
				}
			};

			if (filterIterator.hasNext()) {
				Holder first = filterIterator.next();
				if (filterIterator.hasNext())
					throw new SingularConstraintViolationException("Multiple links of type " + baseComponent + " on target " + component + " (n° " + axe + ") : " + first + ", " + filterIterator.next() + " ...");
			}
			// Snapshot<Holder> holders = ((GenericImpl) component).getHolders((Relation) baseComponent, axe);
			// if (holders.size() > 1)
			// throw new SingularConstraintViolationException("Multiple links of type " + baseComponent + " on target " + component + " (n° " + axe + ") : " + holders);
		}
	}
}
