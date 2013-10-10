package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(InstanceClassConstraintImpl.class)
public class InstanceClassConstraintImpl extends AbstractNoBooleanSimpleConstraintImpl implements Holder {

	@Override
	public void check(Generic modified, Generic type, Serializable value) throws ConstraintViolationException {
		getCurrentCache().<Holder> find(InstanceClassConstraintImpl.class);
		modified.log();
		((GenericImpl) modified).supers[0].log();
		modified.getMeta();
		// if (modified.isConcrete() && ((GenericImpl) modified.getMeta()).getValue(((EngineImpl) modified.getEngine()).getCurrentCache().<Holder> find(InstanceClassConstraintImpl.class)) != null) {
		// Class<?> clazz = (Class<?>) value;
		// if (modified.getValue() != null && !clazz.isAssignableFrom(modified.getValue().getClass()))
		// throw new InstanceClassConstraintViolationException("Wrong value type for generic " + modified + " : should be " + clazz.getSimpleName() + " but is " + modified.getValue().getClass().getSimpleName() + " for type " + type);
		// }
	}

	@Override
	public void checkConsistency(Generic base, Holder value, int axe) throws ConstraintViolationException {
		assert base.isStructural();
		for (Generic instance : ((Type) base).getInstances())
			if (!value.getValue().equals(AxedPropertyClass.class) && !instance.getValue().getClass().equals(value.getValue()))
				throw new InstanceClassConstraintViolationException("Wrong type of instance");
	}
}
