package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

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

	public abstract void check(Generic modified, Holder valueBaseComponent) throws ConstraintViolationException;

	public abstract void checkConsistency(Generic base, Holder valueBaseComponent, Generic attribute, AxedPropertyClass key) throws ConstraintViolationException;

	// @Override
	// public int compareTo(AbstractConstraintImpl otherConstraint) {
	// int result = Integer.valueOf(getPriority()).compareTo(Integer.valueOf(otherConstraint.getPriority()));
	// if (result != 0)
	// return result;
	// return this.getClass().getName().compareTo(otherConstraint.getClass().getName());
	// }

	// public AbstractConstraintImpl bindAxedConstraint(int pos) {
	// Generic implicit = getEngine().bindPrimary(Generic.class, new AxedPropertyClass(getClass(), pos), SystemGeneric.STRUCTURAL, true);
	// return getCurrentCache().<GenericImpl> find(MapInstance.class).bind(getClass(), implicit, this, getBasePos(this), false, new Generic[] {});
	// }
	//
	// public <T extends AbstractConstraintImpl> T findAxedConstraint(int pos) {
	// Generic implicit = getEngine().findPrimary(new AxedPropertyClass(getClass(), pos), SystemGeneric.STRUCTURAL);
	// if (implicit == null)
	// return null;
	// return getCurrentCache().<GenericImpl> find(MapInstance.class).<T> find(implicit, this, getBasePos(this), new Generic[] {});
	// }

	public static class AxedPropertyClass implements Serializable {
		private static final long serialVersionUID = 182492104604984855L;

		private final Class<?> clazz;
		private final int axe;

		public AxedPropertyClass(Class<?> clazz, int axe) {
			this.clazz = clazz;
			this.axe = axe;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public int getAxe() {
			return axe;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AxedPropertyClass))
				return false;
			AxedPropertyClass compare = (AxedPropertyClass) obj;
			return clazz.equals(compare.getClazz()) && axe == compare.axe;
		}

		@Override
		public int hashCode() {
			return clazz.hashCode();
		}

		@Override
		public String toString() {
			return "class : " + clazz + ", axe : " + axe;
		}
	}
}