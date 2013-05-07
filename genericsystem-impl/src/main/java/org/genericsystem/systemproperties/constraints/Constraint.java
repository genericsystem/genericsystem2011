package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.genericsystem.snapshot.AbstractSnapshot;

/**
 * @author Nicolas Feybesse
 * 
 */
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

	public static class ConstraintValue {

		private Serializable value;

		private Generic constraintBaseType;

		public ConstraintValue(Serializable value, Generic constraintBaseType) {
			this.value = value;
			this.constraintBaseType = constraintBaseType;
		}

		public Serializable getValue() {
			return value;
		}

		public Generic getConstraintBaseType() {
			return constraintBaseType;
		}

	}

	// TODO it's clean ?
	protected static Snapshot<ConstraintValue> getConstraintValues(final Context context, final Generic modified, final Class<? extends Constraint> clazz) {
		return new AbstractSnapshot<ConstraintValue>() {
			@Override
			public Iterator<ConstraintValue> iterator() {
				// TODO base pos KK
				Iterator<ConstraintValue> iterator = new AbstractProjectionIterator<Holder, ConstraintValue>(((GenericImpl) modified).getHolders(context, ((AbstractContext) context).<Attribute> find(clazz), Statics.BASE_POSITION).iterator()) {
					@Override
					public ConstraintValue project(Holder generic) {
						return new ConstraintValue(generic.getValue(), generic.getBaseComponent());
					};
				};
				if (!iterator.hasNext() && clazz.getAnnotation(SystemGeneric.class).defaultBehavior()) {
					List<ConstraintValue> constraintValues = new ArrayList<>(modified.getComponentsSize());
					for (int i = 0; i <= modified.getComponentsSize(); i++)
						constraintValues.add(new ConstraintValue(i, modified));
					return constraintValues.iterator();
				}
				return iterator;
			}
		};
	}
}