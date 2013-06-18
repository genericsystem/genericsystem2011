package org.generic.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Engine;
import org.genericsystem.generic.Attribute;

@SystemGeneric
@Components(Engine.class)
public class PropertiesMapProvider extends AbstractMapProvider {

	static final String NAME = "MAP";

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

}
