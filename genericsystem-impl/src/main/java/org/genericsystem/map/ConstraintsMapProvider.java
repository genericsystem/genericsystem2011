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
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider.SingularConstraintImpl;
import org.genericsystem.systemproperties.BooleanSystemProperty;
import org.genericsystem.systemproperties.constraints.AbstractAxedConstraintImpl;
import org.genericsystem.systemproperties.constraints.AbstractSimpleConstraintImpl;

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
	public static class SingularConstraintImpl extends AbstractAxedConstraintImpl implements Holder, BooleanSystemProperty {

		@SystemGeneric(SystemGeneric.CONCRETE)
		@Components(SingularConstraintImpl.class)
		@Extends(ConstraintsMapProvider.ConstraintValue.class)
		@BooleanValue(false)
		public static class DefaultValue extends GenericImpl implements Holder {
		}

		@Override
		public void check(Generic baseComponent, Generic modified, int axe) throws ConstraintViolationException {
			Generic component = ((Link) modified).getComponent(axe);
			Snapshot<Holder> holders = ((GenericImpl) component).getHolders((Relation) baseComponent, axe);
			if (holders.size() > 1)
				throw new SingularConstraintViolationException("Multiple links of type " + baseComponent + " on target " + component + " (n° " + axe + ") : " + holders);
		}
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(MapInstance.class)
	@Extends(ConstraintKey.class)
	@SingularConstraint
	public static class PropertyConstraintImpl extends AbstractSimpleConstraintImpl implements Holder, BooleanSystemProperty {

		@Override
		public void check(final Generic baseComponent, final Generic modified) throws ConstraintViolationException {
			// Generic component = ((Link) modified).getComponent(axe);
			// Snapshot<Holder> holders = ((GenericImpl) component).getHolders((Relation) baseComponent, axe);
			// if (holders.size() > 1)
			// throw new PropertyConstraintViolationException("Multiple links of type " + baseComponent + " on target " + component + " (n° " + axe + ") : " + holders);

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
