package org.genericsystem.systemproperties.constraints.simple;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider;
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
@Dependencies(UniqueValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanSimpleConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(UniqueValueConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type) throws ConstraintViolationException {
		//if (!modified.isStructural()) {
		/* Get all instances of type and checks that there is only one instance with the same value */
		log.info("Type: " + type + " " + ((Type) type).getAllInstances());
		for (Generic generic : ((Type) type).getAllInstances())
			if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
				throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
		/*} else {
			/* Get all instances of modified structural and checks that there is only one instance
		Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue());
		if (iterator.hasNext()) {
			iterator.next();
			if (iterator.hasNext()) {
				throw new UniqueStructuralValueConstraintViolationException("modified : " + modified.info());
			}
		}
	}*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkConsistency(Generic base, Holder valueHolder, int axe) throws ConstraintViolationException {
		Set<Serializable> values = new HashSet<>();
		for (Generic attributeNode : ((Type) base).getAllInstances()) {
			Serializable value = attributeNode.getValue();
			if (value != null)
				if (!values.add(value))
					throw new UniqueValueConstraintViolationException("Duplicate value : " + value);
		}
	}
}
