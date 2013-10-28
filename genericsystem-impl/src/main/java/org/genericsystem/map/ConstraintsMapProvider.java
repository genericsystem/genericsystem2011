package org.genericsystem.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.constraints.AliveConstraintImpl;
import org.genericsystem.constraints.AloneAutomaticsConstraintImpl;
import org.genericsystem.constraints.ConcreteInheritanceConstraintImpl;
import org.genericsystem.constraints.InstanceClassConstraintImpl;
import org.genericsystem.constraints.OptimisticLockConstraintImpl;
import org.genericsystem.constraints.PhantomConstraintImpl;
import org.genericsystem.constraints.PropertyConstraintImpl;
import org.genericsystem.constraints.RequiredConstraintImpl;
import org.genericsystem.constraints.SingletonConstraintImpl;
import org.genericsystem.constraints.SingularConstraintImpl;
import org.genericsystem.constraints.SizeConstraintImpl;
import org.genericsystem.constraints.StructuralNamingConstraintImpl;
import org.genericsystem.constraints.SuperRuleConstraintImpl;
import org.genericsystem.constraints.UnduplicateBindingConstraintImpl;
import org.genericsystem.constraints.UniqueValueConstraintImpl;
import org.genericsystem.constraints.VirtualConstraintImpl;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * @param <T>
 * 
 */
@SystemGeneric
@Components(EngineImpl.class)
@Dependencies({ RequiredConstraintImpl.class, SingularConstraintImpl.class, SizeConstraintImpl.class, AliveConstraintImpl.class, AloneAutomaticsConstraintImpl.class, ConcreteInheritanceConstraintImpl.class, InstanceClassConstraintImpl.class,
		OptimisticLockConstraintImpl.class, PhantomConstraintImpl.class, PropertyConstraintImpl.class, SingletonConstraintImpl.class, SuperRuleConstraintImpl.class, UnduplicateBindingConstraintImpl.class, /* UniqueStructuralValueConstraintImpl.class, */
		UniqueValueConstraintImpl.class, VirtualConstraintImpl.class, StructuralNamingConstraintImpl.class })
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
	public static class ConstraintKey extends GenericImpl implements Attribute {}

	@SystemGeneric
	@Extends(NoInheritanceSystemType.class)
	@Components(ConstraintsMapProvider.class)
	@InstanceValueClassConstraint(AxedPropertyClass.class)
	public static class NoInheritanceConstraintKey extends ConstraintKey implements Attribute {}

	@SystemGeneric
	@Extends(NoInheritanceSystemType.class)
	@Components(ConstraintKey.class)
	@SingularConstraint
	public static class ConstraintValue extends GenericImpl implements Attribute {}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.class)
	@Components(EngineImpl.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {}
}
