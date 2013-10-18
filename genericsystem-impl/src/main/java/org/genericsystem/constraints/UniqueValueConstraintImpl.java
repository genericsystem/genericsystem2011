package org.genericsystem.constraints;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * 
 * 
 * @author Nicolas Feybesse
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type, int axe) throws ConstraintViolationException {
		if (!modified.isStructural()) {
			for (Generic generic : ((Type) type).getAllInstances())
				if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
					throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
		} else {
			Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue());
			if (iterator.hasNext()) {
				iterator.next();
				if (iterator.hasNext()) {
					throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
				}
			}
		}
		Set<Serializable> values = new HashSet<>();
		for (Generic attributeNode : ((Type) type).getAllInstances()) {
			Serializable value = attributeNode.getValue();
			if (value != null)
				if (!values.add(value))
					throw new UniqueValueConstraintViolationException("Duplicate value : " + value);
		}
	}
}
