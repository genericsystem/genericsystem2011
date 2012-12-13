package org.genericsystem.impl.constraints;

import org.genericsystem.api.annotation.Priority;

public abstract class AbstractConstraint implements Constraint {
	
	private static final long serialVersionUID = -6936080356593512744L;
	
	@Override
	public final int getPriority() {
		Priority annotation = getClass().getAnnotation(Priority.class);
		return annotation != null ? annotation.value() : 0;
	}
	
	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
	
	@Override
	public boolean isImmediatelyCheckable() {
		return true;
	}
	
	@Override
	public int compareTo(Constraint otherConstraint) {
		int result = Integer.valueOf(getPriority()).compareTo(Integer.valueOf(otherConstraint.getPriority()));
		if (result != 0)
			return result;
		return this.getClass().getName().compareTo(otherConstraint.getClass().getName());
	}
	
}