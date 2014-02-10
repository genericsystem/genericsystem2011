package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.AloneAutomaticsConstraintViolationException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ AloneAutomaticsConstraintImpl.DefaultKey.class, AloneAutomaticsConstraintImpl.DefaultValue.class })
public class AloneAutomaticsConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Meta(AloneAutomaticsConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(AloneAutomaticsConstraintImpl.class)
	public static class DefaultKey {
	}

	@SystemGeneric
	@Meta(ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {
	}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		if (constraintBase.isAlive() && constraintBase.getInheritings().isEmpty() && constraintBase.getComposites().isEmpty())
			throw new AloneAutomaticsConstraintViolationException();
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE);
	}
}
