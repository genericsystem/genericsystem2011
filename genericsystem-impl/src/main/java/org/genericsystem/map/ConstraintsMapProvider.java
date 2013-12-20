package org.genericsystem.map;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.NoInheritance;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.constraints.AliveConstraintImpl;
import org.genericsystem.constraints.AloneAutomaticsConstraintImpl;
import org.genericsystem.constraints.InstanceClassConstraintImpl;
import org.genericsystem.constraints.MetaLevelConstraintImpl;
import org.genericsystem.constraints.OptimisticLockConstraintImpl;
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
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * @param <T>
 * 
 */
@SystemGeneric
@Components(EngineImpl.class)
@Dependencies({ MapInstance.class, MetaLevelConstraintImpl.class, RequiredConstraintImpl.class, SingularConstraintImpl.class, SizeConstraintImpl.class, AliveConstraintImpl.class, AloneAutomaticsConstraintImpl.class, InstanceClassConstraintImpl.class,
		OptimisticLockConstraintImpl.class, PropertyConstraintImpl.class, SingletonConstraintImpl.class, SuperRuleConstraintImpl.class, UnduplicateBindingConstraintImpl.class, UniqueValueConstraintImpl.class, VirtualConstraintImpl.class,
		StructuralNamingConstraintImpl.class })
public class ConstraintsMapProvider extends AbstractMapProvider<AxedPropertyClass, Serializable> {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getKeyAttributeClass(AxedPropertyClass key) {
		return (Class<T>) (key == null ? ConstraintKey.class : key.getClazz());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) ConstraintValue.class;
	}

	@SystemGeneric
	@Components(ConstraintsMapProvider.class)
	@InstanceValueClassConstraint(AxedPropertyClass.class)
	public static class ConstraintKey extends GenericImpl {
	}

	@SystemGeneric
	@NoInheritance
	@Components(ConstraintKey.class)
	@SingularConstraint
	public static class ConstraintValue extends GenericImpl {
	}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.class)
	@Components(EngineImpl.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl {
	}
}
