package org.genericsystem.map;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.NoInheritance;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.map.PropertiesMapProvider.PropertyKey;
import org.genericsystem.map.PropertiesMapProvider.PropertyValue;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(EngineImpl.class)
@Dependencies({ PropertyKey.class, PropertyValue.class })
public class PropertiesMapProvider extends AbstractMapProvider<Serializable, Serializable> {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getKeyAttributeClass(Serializable key) {
		return (Class<T>) PropertyKey.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) PropertyValue.class;
	}

	@SystemGeneric
	@Components(PropertiesMapProvider.class)
	@InstanceValueClassConstraint(Serializable.class)
	public static class PropertyKey extends GenericImpl {
	}

	@SystemGeneric
	@Components(PropertyKey.class)
	@SingularConstraint
	@NoInheritance
	// @RequiredConstraint
	public static class PropertyValue extends GenericImpl {
	}

	@SystemGeneric
	@Meta(PropertiesMapProvider.class)
	@Components(EngineImpl.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl {
	}

}
