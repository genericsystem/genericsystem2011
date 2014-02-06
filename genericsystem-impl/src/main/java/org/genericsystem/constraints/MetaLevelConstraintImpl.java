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
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.MetaLevelConstraintViolationException;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ MetaLevelConstraintImpl.DefaultKey.class, MetaLevelConstraintImpl.DefaultValue.class })
public class MetaLevelConstraintImpl extends AbstractBooleanNoAxedConstraintImpl {

	@SystemGeneric
	@Meta(MetaLevelConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(MetaLevelConstraintImpl.class)
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
		for (Generic component : modified.getComponents())
			if (component.getMetaLevel() > modified.getMetaLevel())
				throw new MetaLevelConstraintViolationException(component + " must have a meta level lower than " + modified);
	}

}
