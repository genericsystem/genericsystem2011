package org.genericsystem.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.genericsystem.systemproperties.NoReferentialIntegritySystemProperty;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(EngineImpl.class)
@Dependencies({ NoReferentialIntegritySystemProperty.class, CascadeRemoveSystemProperty.class })
public class SystemPropertiesMapProvider extends AbstractMapProvider<AxedPropertyClass, Boolean> {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getKeyAttributeClass() {
		return (Class<T>) SystemPropertyKey.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) SystemPropertyValue.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends GenericImpl> Class<T> getSpecializationClass(AxedPropertyClass key) {
		return (Class<T>) key.getClazz();
	}

	@SystemGeneric
	@Components(SystemPropertiesMapProvider.class)
	@InstanceValueClassConstraint(AxedPropertyClass.class)
	public static class SystemPropertyKey extends GenericImpl implements Attribute {}

	@SystemGeneric
	@Components(SystemPropertyKey.class)
	@SingularConstraint
	@Extends(NoInheritanceSystemType.class)
	// @RequiredConstraint
	// @InheritanceDisabled
	public static class SystemPropertyValue extends GenericImpl implements Attribute {}

	@SystemGeneric
	@Extends(meta = SystemPropertiesMapProvider.class)
	@Components(EngineImpl.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {}
}
