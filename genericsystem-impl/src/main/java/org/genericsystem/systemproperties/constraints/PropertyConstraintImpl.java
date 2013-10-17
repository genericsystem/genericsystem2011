package org.genericsystem.systemproperties.constraints;

import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(PropertyConstraintImpl.class)
public class PropertyConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@Override
	public void check(final Generic modified, final Generic type,int axe) throws ConstraintViolationException {
		if (modified.isAttribute()) {
			// TODO KK
			for (final Generic inheriting : ((GenericImpl) ((Holder) modified).getBaseComponent()).getAllInheritings()) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Iterator<Generic> it = new AbstractFilterIterator<Generic>((Iterator) inheriting.getHolders((Attribute) type).iterator()) {
					@Override
					public boolean isSelected() {
						for (int componentPos = 1; componentPos < next.getComponents().size(); componentPos++)
							if (!Objects.equals(((Holder) next).getComponent(componentPos), ((Holder) type).getComponent(componentPos)))
								return false;
						return true;
					}
				};
				if (it.hasNext()) {
					Generic value = it.next();
					if (it.hasNext())
						throw new PropertyConstraintViolationException(value.info() + it.next().info());
				}
			}
			return;
		}
		if (new AbstractFilterIterator<Generic>(((GenericImpl) type).getAllInstances().iterator()) {
			@Override
			public boolean isSelected() {
				return !next.equals(modified) && Objects.equals(next.getValue(), modified.getValue());
			}
		}.hasNext())
			throw new PropertyConstraintViolationException("");
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}
	@Override
	public void checkConsistency(Generic modified,Holder valueConstraint, int axe) throws ConstraintViolationException {
		// TODO Auto-generated method stub
		
	}

}