package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.AxedPropertyClass;
import org.genericsystem.core.Generic;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;

public abstract class AbstractNoBooleanConstraintImpl extends AbstractConstraintImpl {

	@Override
	public void check(Generic modified, Holder valueBaseComponent) throws ConstraintViolationException {
		check(modified, valueBaseComponent.<Attribute> getBaseComponent().<Attribute> getBaseComponent().getBaseComponent(), valueBaseComponent.getValue());
	}

	@Override
	public void checkConsistency(Generic base, Holder valueBaseComponent, Generic attribute, AxedPropertyClass key) throws ConstraintViolationException {
		checkConsistency(base, attribute, key, valueBaseComponent.getValue(getCurrentCache().<Holder> find(ConstraintsMapProvider.ConstraintValue.class)));
	}

	public abstract void check(Generic modified, Generic baseComponent, Serializable value) throws ConstraintViolationException;

	public abstract void checkConsistency(Generic base, Generic attribute, AxedPropertyClass key, Serializable value) throws ConstraintViolationException;
}