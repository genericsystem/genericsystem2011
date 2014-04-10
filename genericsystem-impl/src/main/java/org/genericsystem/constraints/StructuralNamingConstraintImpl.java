package org.genericsystem.constraints;

import java.util.Objects;

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
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.snapshot.FunctionalSnapshot;

/**
 * @author Nicolas Feybesse
 *
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ StructuralNamingConstraintImpl.DefaultKey.class, StructuralNamingConstraintImpl.DefaultValue.class })
public class StructuralNamingConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Meta(StructuralNamingConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(StructuralNamingConstraintImpl.class)
	public static class DefaultKey {
	}

	@SystemGeneric
	@Meta(ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		if (!modified.isStructural())
			return;
		if (modified.getComponents().isEmpty()) {
			FunctionalSnapshot<Generic> snapshot = ((GenericImpl) modified.getEngine()).getAllInstancesSnapshot().filter(next -> Objects.equals(modified.getValue(), modified));
			if (snapshot.size() > 1)
				throw new UniqueStructuralValueConstraintViolationException(snapshot.get(0).info() + snapshot.get(1).info());
		} else
			for (int i = 0; i < modified.getComponents().size(); i++)
				for (Generic inherited : ((GenericImpl) ((GenericImpl) modified).getComponents().get(i)).getAllInheritings()) {
					FunctionalSnapshot<Generic> snapshot = ((GenericImpl) inherited).holdersSnapshot(Statics.STRUCTURAL, getCurrentCache().getMetaAttribute(), Statics.MULTIDIRECTIONAL).filter(next -> Objects.equals(modified.getValue(), next.getValue()));
					if (snapshot.size() > 1)
						throw new UniqueStructuralValueConstraintViolationException(inherited.info() + snapshot.get(0).info() + snapshot.get(1).info());
				}
	}

}
