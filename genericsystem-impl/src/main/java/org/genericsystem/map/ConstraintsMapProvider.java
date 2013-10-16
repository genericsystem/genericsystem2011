package org.genericsystem.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.genericsystem.systemproperties.constraints.AliveConstraintImpl;
import org.genericsystem.systemproperties.constraints.AloneAutomaticsConstraintImpl;
import org.genericsystem.systemproperties.constraints.ConcreteInheritanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.InstanceClassConstraintImpl;
import org.genericsystem.systemproperties.constraints.OptimisticLockConstraintImpl;
import org.genericsystem.systemproperties.constraints.PhantomConstraintImpl;
import org.genericsystem.systemproperties.constraints.PropertyConstraintImpl;
import org.genericsystem.systemproperties.constraints.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.SingletonConstraintImpl;
import org.genericsystem.systemproperties.constraints.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.SizeConstraintImpl;
import org.genericsystem.systemproperties.constraints.SuperRuleConstraintImpl;
import org.genericsystem.systemproperties.constraints.UnduplicateBindingConstraintImpl;
import org.genericsystem.systemproperties.constraints.UniqueStructuralValueConstraintImpl;
import org.genericsystem.systemproperties.constraints.UniqueValueConstraintImpl;
import org.genericsystem.systemproperties.constraints.VirtualConstraintImpl;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * @param <T>
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ RequiredConstraintImpl.class, SingularConstraintImpl.class, SizeConstraintImpl.class, AliveConstraintImpl.class, AloneAutomaticsConstraintImpl.class, ConcreteInheritanceConstraintImpl.class, InstanceClassConstraintImpl.class,
		OptimisticLockConstraintImpl.class, PhantomConstraintImpl.class, PropertyConstraintImpl.class, SingletonConstraintImpl.class, SuperRuleConstraintImpl.class, UnduplicateBindingConstraintImpl.class, UniqueStructuralValueConstraintImpl.class,
		UniqueValueConstraintImpl.class, VirtualConstraintImpl.class })
public class ConstraintsMapProvider extends AbstractMapProvider<AxedPropertyClass, Boolean> {

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

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends GenericImpl> Class<T> getSpecializationClass(AxedPropertyClass key) {
		return (Class<T>) key.getClazz();
	}

	@SystemGeneric
	@Components(ConstraintsMapProvider.class)
	@InstanceValueClassConstraint(AxedPropertyClass.class)
	public static class ConstraintKey extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Extends(NoInheritanceSystemType.class)
	@Components(ConstraintKey.class)
	@SingularConstraint
	// @RequiredConstraint
	// @InheritanceDisabled
	public static class ConstraintValue extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.class)
	@Components(Engine.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {
	}
}
