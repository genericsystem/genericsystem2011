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
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.GetGenericConstraintVioliationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ GetGenericConstraintImpl.DefaultKey.class, GetGenericConstraintImpl.DefaultValue.class })
public class GetGenericConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Meta(GetGenericConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(GetGenericConstraintImpl.class)
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
		try {
			Generic generic = ((GenericImpl) modified.getMeta()).getGeneric(modified.getValue(), (Generic[]) modified.getComponents().toArray());
			if (generic != modified)
				throw new GetGenericConstraintVioliationException("get : " + generic.info() + " for search : " + modified.info());
		} catch (Exception e) {
			throw new GetGenericConstraintVioliationException(e.getMessage());
		}
	}
}
