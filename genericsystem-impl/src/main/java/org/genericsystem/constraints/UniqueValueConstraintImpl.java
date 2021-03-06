package org.genericsystem.constraints;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * 
 * 
 * @author Nicolas Feybesse
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
public class UniqueValueConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		Set<Serializable> values = new HashSet<>();
		for (Generic attributeNode : ((Type) constraintBase).getAllInstances()) {
			if (attributeNode.getValue() != null && !values.add(attributeNode.getValue()))
				throw new UniqueValueConstraintViolationException("Duplicate value : " + attributeNode.getValue());
		}
	}

}
