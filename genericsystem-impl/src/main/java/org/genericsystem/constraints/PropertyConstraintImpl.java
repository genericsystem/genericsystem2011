package org.genericsystem.constraints;

import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.snapshot.FunctionalSnapshot;

@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class PropertyConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@Override
	public void check(final Generic constraintBase, final Generic modified) throws ConstraintViolationException {
		if (modified.isAttribute()) {
			for (final Generic inheriting : ((GenericImpl) ((Holder) modified).getBaseComponent()).getAllInheritings()) {
				FunctionalSnapshot<Holder> snapshot = ((GenericImpl) inheriting).getHolders((Attribute) constraintBase).filter(next -> {
					for (int componentPos = 1; componentPos < next.getComponents().size(); componentPos++)
						if (!Objects.equals(next.getComponent(componentPos), ((Holder) constraintBase).getComponent(componentPos)))
							return false;
					return true;
				});
				if (snapshot.size() > 1)
					throw new PropertyConstraintViolationException(snapshot.get(0).info() + snapshot.get(1).info());
			}
			return;
		}
		if (!(((GenericImpl) constraintBase).getAllInstances().filter(next -> !next.equals(modified) && Objects.equals(next.getValue(), modified.getValue())).isEmpty()))
			throw new PropertyConstraintViolationException("");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}
}
