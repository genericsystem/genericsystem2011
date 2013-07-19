package org.genericsystem.map;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.AliveConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.AloneAutomaticsConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.ConcreteInheritanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.InstanceClassConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PropertyConstraintImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ RequiredConstraintImpl.class, SingularConstraintImpl.class, AliveConstraintImpl.class, AloneAutomaticsConstraintImpl.class, ConcreteInheritanceConstraintImpl.class, InstanceClassConstraintImpl.class, PropertyConstraintImpl.class })
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
}
