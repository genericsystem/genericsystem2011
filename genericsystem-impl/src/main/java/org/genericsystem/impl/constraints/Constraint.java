package org.genericsystem.impl.constraints;

import java.io.Serializable;
import java.util.Iterator;
import org.genericsystem.api.annotation.Priority;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.AbstractContext;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;
import org.genericsystem.impl.iterator.AbstractFilterIterator;
import org.genericsystem.impl.snapshot.AbstractSnapshot;

public abstract class Constraint implements Comparable<Constraint>, Serializable {
	
	private static final long serialVersionUID = -3257819220762195050L;
	
	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}
	
	public abstract void check(Context context, Generic modified) throws ConstraintViolationException;
	
	public final int getPriority() {
		Priority annotation = getClass().getAnnotation(Priority.class);
		return annotation != null ? annotation.value() : 0;
	}
	
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
	
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
	
	protected Snapshot<Value> getConstraintInstances(final Context context, final Generic modified, final Class<? extends Constraint> clazz) {
		return new AbstractSnapshot<Value>() {
			@Override
			public Iterator<Value> iterator() {
				return new AbstractFilterIterator<Value>(((GenericImpl) modified).<Value> mainIterator(context, ((AbstractContext) context).find(clazz), SystemGeneric.CONCRETE, Statics.BASE_POSITION, false)) {
					@Override
					public boolean isSelected() {
						return !Boolean.FALSE.equals(next.getValue());
					}
				};
			}
		};
	}
}