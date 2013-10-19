package org.genericsystem.impl;

import java.io.Serializable;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.impl.MyMapProvider.MyValue;
import org.genericsystem.map.AbstractMapProvider;

@SystemGeneric
@Components(EngineImpl.class)
@Dependencies(MyValue.class)
public class MyMapProvider extends AbstractMapProvider<Serializable, Serializable> {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Attribute> Class<T> getKeyAttributeClass() {
		return (Class<T>) MyKey.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Attribute> Class<T> getValueAttributeClass() {
		return (Class<T>) MyValue.class;
	}

	@SystemGeneric
	@Components(MyMapProvider.class)
	public static class MyKey extends GenericImpl implements Attribute {

	}

	@SystemGeneric
	@Components(MyKey.class)
	@SingularConstraint
	@RequiredConstraint
	public static class MyValue extends GenericImpl implements Attribute {

	}

}
