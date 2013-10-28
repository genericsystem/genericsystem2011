package org.genericsystem.constraints;

import java.util.Iterator;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
import org.genericsystem.systemproperties.constraints.simple.AbstractBooleanSimpleConstraintImpl;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(StructuralNamingConstraintImpl.DefaultValue.class)
@AxedConstraintValue(StructuralNamingConstraintImpl.class)
public class StructuralNamingConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(StructuralNamingConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic instanceToCheck, Generic constraintBase, Holder constraintValue, CheckingType checkingType, int axe) throws ConstraintViolationException {
		if (!constraintBase.isStructural() && constraintBase.getComponentsSize() == 0)
			return;
		Generic[] components = ((GenericImpl) constraintBase).getComponentsArray();
		for (int i = 0; i < constraintBase.getComponentsSize(); i++)
			for (Generic inherited : ((GenericImpl) components[i]).getAllInheritings()) {
				Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) inherited).holdersIterator(Statics.STRUCTURAL, getCurrentCache().getMetaAttribute(), Statics.MULTIDIRECTIONAL, constraintBase.getValue() == null), constraintBase.getValue());
				if (iterator.hasNext()) {
					iterator.next();
					if (iterator.hasNext())
						throw new UniqueStructuralValueConstraintViolationException(iterator.next().info());
				}
			}
	}

}
