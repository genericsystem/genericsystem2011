package org.genericsystem.constraints;

import org.genericsystem.annotation.Priority;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;

public abstract class AbstractConstraintImpl extends GenericImpl {

	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}

	public final int getPriority() {
		Priority annotation = getClass().getAnnotation(Priority.class);
		return annotation != null ? annotation.value() : 0;
	}

	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE);
	}

	public boolean isImmediatelyCheckable() {
		return true;
	}

	public boolean isImmediatelyConsistencyCheckable() {
		return true;
	}

	public abstract void check(Generic constraintBase, Generic modified, Holder constraintValue) throws ConstraintViolationException;

	public abstract static class AbstractBooleanConstraintImpl extends AbstractConstraintImpl {
		@Override
		public void check(Generic constraintBase, Generic modified, Holder constraintValue) throws ConstraintViolationException {
			if (Boolean.TRUE.equals(constraintValue.getValue()))
				check(constraintBase, modified);
		}

		public abstract void check(Generic constraintBase, Generic modified) throws ConstraintViolationException;
	}

}