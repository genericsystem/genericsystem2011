package org.genericsystem.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.map.PropertiesMapProvider.PropertyKey;
import org.genericsystem.map.PropertiesMapProvider.PropertyValue;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Components(Engine.class)
@Dependencies({ PropertyKey.class, PropertyValue.class })
public class PropertiesMapProvider extends AbstractMapProvider {

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
	@RequiredConstraint
	public static class PropertyValue extends GenericImpl implements Attribute {

	}

}
