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

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(UniqueStructuralValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueStructuralValueConstraintImpl.class)
public class UniqueStructuralValueConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(UniqueStructuralValueConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type, int axe) throws ConstraintViolationException {
		if (!modified.isStructural() && modified.getComponentsSize() == 0)
			return;
		Generic[] components = ((GenericImpl) modified).getComponentsArray();
		for (int i = 0; i < modified.getComponentsSize(); i++)
			for (Generic inherited : ((GenericImpl) components[i]).getAllInheritings()) {
				Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) inherited).holdersIterator(Statics.STRUCTURAL, getCurrentCache().getMetaAttribute(), Statics.MULTIDIRECTIONAL, modified.getValue() == null), modified.getValue());
				if (iterator.hasNext()) {
					iterator.next();
					if (iterator.hasNext())
						throw new UniqueStructuralValueConstraintViolationException(iterator.next().info());
				}
			}
	}
}
