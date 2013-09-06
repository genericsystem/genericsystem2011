package org.genericsystem.map;

import java.io.Serializable;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.PropertiesMapProvider.PropertyKey;
import org.genericsystem.map.PropertiesMapProvider.PropertyValue;
import org.genericsystem.systemproperties.NoInheritanceSystemType;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ PropertyKey.class, PropertyValue.class })
public class PropertiesMapProvider extends AbstractMapProvider<Serializable, Serializable> {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getKeyAttributeClass() {
		return (Class<T>) PropertyKey.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) PropertyValue.class;
	}

	@SystemGeneric
	@Components(PropertiesMapProvider.class)
	public static class PropertyKey extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Components(PropertyKey.class)
	@SingularConstraint
	@Extends(NoInheritanceSystemType.class)
	// @RequiredConstraint
	// @InheritanceDisabled
	public static class PropertyValue extends GenericImpl implements Attribute {
	}

	@SystemGeneric
	@Extends(meta = PropertiesMapProvider.class)
	@Components(Engine.class)
	@StringValue(AbstractMapProvider.MAP_VALUE)
	public static class MapInstance extends GenericImpl implements Holder {
	}

}
