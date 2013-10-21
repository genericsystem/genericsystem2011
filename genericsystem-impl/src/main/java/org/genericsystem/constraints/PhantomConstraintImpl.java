package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PhantomConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
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
@Dependencies(PhantomConstraintImpl.DefaultValue.class)
@AxedConstraintValue(PhantomConstraintImpl.class)
public class PhantomConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(PhantomConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {}

	@Override
	public void check(Generic modified, Generic type, int axe) throws ConstraintViolationException {
		Generic[] supers = ((GenericImpl) modified).getSupersArray();
		if (modified.getValue() == null)
			if (modified.getComponentsSize() != 0) {
				if (supers.length != 1)
					throw new PhantomConstraintViolationException(modified.info() + " " + supers[1].info());

				Generic[] components = ((GenericImpl) supers[0]).getComponentsArray();
				Generic[] subComponents = ((GenericImpl) modified).getComponentsArray();
				assert components.length >= subComponents.length;

				int inheritancesCount = 0;
				for (int i = 0; i < subComponents.length; i++) {
					Generic component = components[i];
					Generic subComponent = subComponents[i];
					if (component != subComponent) {
						if (!subComponent.inheritsFrom(component))
							throw new PhantomConstraintViolationException(modified.info() + subComponent.info() + component.info());
						inheritancesCount++;
					}
				}

				if (inheritancesCount != 1)
					throw new PhantomConstraintViolationException(modified.info());
			}
	}

}
