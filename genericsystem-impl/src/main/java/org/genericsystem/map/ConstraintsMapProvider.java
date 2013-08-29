package org.genericsystem.map;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.InheritanceDisabled;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.constraints.AbstractConstraintImpl.AxedPropertyClass;
import org.genericsystem.systemproperties.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.AliveConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.AloneAutomaticsConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.ConcreteInheritanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.InstanceClassConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.OptimisticLockConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PhantomConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.SingletonConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.SuperRuleConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UnduplicateBindingConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UniqueStructuralValueConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UniqueValueConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.VirtualConstraintImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ RequiredConstraintImpl.class, SingularConstraintImpl.class, SizeConstraintImpl.class, AliveConstraintImpl.class, AloneAutomaticsConstraintImpl.class, ConcreteInheritanceConstraintImpl.class, InstanceClassConstraintImpl.class,
		OptimisticLockConstraintImpl.class, PhantomConstraintImpl.class, PropertyConstraintImpl.class, SingletonConstraintImpl.class, SuperRuleConstraintImpl.class, UnduplicateBindingConstraintImpl.class, UniqueStructuralValueConstraintImpl.class,
		UniqueValueConstraintImpl.class, VirtualConstraintImpl.class })
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

	@Override
	protected Class<?> getKeyClass(Serializable key) {
		return ((AxedPropertyClass) key).getClazz();
	}

	@SystemGeneric
	@Components(ConstraintsMapProvider.class)
	public static class ConstraintKey extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Components(ConstraintKey.class)
	@SingularConstraint
	// @RequiredConstraint
	@InheritanceDisabled
	public static class ConstraintValue extends GenericImpl implements Attribute {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Extends(ConstraintsMapProvider.class)
	@Components(Engine.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {
	}
}
