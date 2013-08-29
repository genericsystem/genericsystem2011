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
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.NoReferentialIntegritySystemProperty;
import org.genericsystem.systemproperties.constraints.AbstractConstraintImpl.AxedPropertyClass;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ NoReferentialIntegritySystemProperty.class, CascadeRemoveSystemProperty.class })
public class SystemPropertiesMapProvider extends AbstractMapProvider<Serializable, Boolean> {

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

	@Override
	protected Class<?> getKeyClass(Serializable key) {
		return ((AxedPropertyClass) key).getClazz();
	}

	@SystemGeneric
	@Components(SystemPropertiesMapProvider.class)
	public static class SystemPropertyKey extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Components(SystemPropertyKey.class)
	@SingularConstraint
	// @RequiredConstraint
	@InheritanceDisabled
	public static class SystemPropertyValue extends GenericImpl implements Attribute {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Extends(SystemPropertiesMapProvider.class)
	@Components(Engine.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {
	}
}
