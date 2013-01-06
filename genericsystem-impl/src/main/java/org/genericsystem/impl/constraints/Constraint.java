package org.genericsystem.impl.constraints;

import java.io.Serializable;

import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConstraintViolationException;

public interface Constraint extends Comparable<Constraint>, Serializable {

	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}

	void check(Context context, Generic modified) throws ConstraintViolationException;

	boolean isImmediatelyCheckable();

	boolean isCheckedAt(CheckingType type);

	int getPriority();
	
	Serializable getDefaultValue();
}