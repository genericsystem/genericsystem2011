package org.genericsystem.constraints;

import org.genericsystem.annotation.Priority;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.AxedPropertyClass;

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

	protected Generic getConstraintBase(Holder constraintValue) {
		return constraintValue.<Holder> getBaseComponent().<Holder> getBaseComponent().getBaseComponent();
	}

	protected int getAxe(Holder constraintValue) {
		return constraintValue.<Holder> getBaseComponent().<AxedPropertyClass> getValue().getAxe();
	}

	public abstract void check(Generic modified, Holder constraintValue) throws ConstraintViolationException;

	public void checkConsistency(Holder constraintValue) throws ConstraintViolationException {
		check(constraintValue, constraintValue);
	}

	public abstract static class AbstractAxedConstraintImpl extends AbstractConstraintImpl {

		@Override
		public void check(Generic modified, Holder constraintValue) throws ConstraintViolationException {
			Type component = ((GenericImpl) modified).getComponent(getAxe(constraintValue));
			internalCheck(component != null ? component : modified, constraintValue);
		}

		@Override
		public void checkConsistency(Holder constraintValue) throws ConstraintViolationException {
			Generic constraintBase = getConstraintBase(constraintValue);
			Type component = ((GenericImpl) constraintBase).getComponent(getAxe(constraintValue));
			if (component != null)
				for (Generic instance : component.getAllInstances())
					internalCheck(instance, constraintValue);
		}

		public abstract void internalCheck(Generic modified, Holder constraintValue) throws ConstraintViolationException;
	}

	public abstract static class AbstractBooleanAxedConstraintImpl extends AbstractAxedConstraintImpl {
		@Override
		public void internalCheck(Generic modified, Holder constraintValue) throws ConstraintViolationException {
			if (!Boolean.FALSE.equals(constraintValue.getValue()))
				check(getConstraintBase(constraintValue), modified);
		}

		public abstract void check(Generic constraintBase, Generic modified) throws ConstraintViolationException;
	}

	public abstract static class AbstractBooleanNoAxedConstraintImpl extends AbstractConstraintImpl {
		@Override
		public void check(Generic modified, Holder constraintValue) throws ConstraintViolationException {
			if (!Boolean.FALSE.equals(constraintValue.getValue()))
				check(getConstraintBase(constraintValue), modified);
		}

		public abstract void check(Generic constraintBase, Generic modified) throws ConstraintViolationException;
	}

}