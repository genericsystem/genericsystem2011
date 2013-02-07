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
import org.genericsystem.impl.iterator.AbstractProjectorAndFilterIterator;
import org.genericsystem.impl.iterator.ArrayIterator;
import org.genericsystem.impl.snapshot.AbstractSnapshot;
import org.genericsystem.impl.system.ComponentPosValue;

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

	public class ConstraintValue {

		private ComponentPosValue<Serializable> value;

		private Generic constraintType;

		public ConstraintValue(ComponentPosValue<Serializable> value, Generic constraintType) {
			this.value = value;
			this.constraintType = constraintType;
		}

		public ComponentPosValue<Serializable> getValue() {
			return value;
		}

		public Generic getConstraintType() {
			return constraintType;
		}

	}

	// TODO KK
	protected Snapshot<ConstraintValue> getConstraintValues(final Context context, final Generic modified, final Class<? extends Constraint> clazz) {
		Snapshot<ConstraintValue> snapshot = new AbstractSnapshot<ConstraintValue>() {
			@Override
			public Iterator<ConstraintValue> iterator() {
				return new AbstractProjectorAndFilterIterator<Value, ConstraintValue>(((GenericImpl) modified).<Value> mainIterator(context, ((AbstractContext) context).find(clazz), SystemGeneric.CONCRETE, Statics.BASE_POSITION)) {

					@Override
					public boolean isSelected() {
						return !Boolean.FALSE.equals(next.<ComponentPosValue<Serializable>> getValue().getValue());
					}

					@Override
					protected ConstraintValue project() {
						return new ConstraintValue(next.<ComponentPosValue<Serializable>> getValue(), next.getBaseComponent());
						// <Serializable>(next.<ComponentPosValue<Serializable>> getValue().getComponentPos(), (Serializable) next.getBaseComponent());//
						// next.<ComponentPosValue<Serializable>> getValue();
					}
				};

				// return new AbstractFilterIterator<Value>(((GenericImpl) modified).<Value> mainIterator(context, ((AbstractContext) context).find(clazz), SystemGeneric.CONCRETE, Statics.BASE_POSITION)) {
				// @Override
				// public boolean isSelected() {
				// return !Boolean.FALSE.equals(next.<ComponentPosValue<Serializable>> getValue().getValue());
				// }
				// };
			}
		};
		if (clazz.getAnnotation(SystemGeneric.class).defaultBehavior() && snapshot.isEmpty())
			return new AbstractSnapshot<ConstraintValue>() {
				@Override
				public Iterator<ConstraintValue> iterator() {
					return new ArrayIterator<ConstraintValue>(new ConstraintValue[] { new ConstraintValue(new ComponentPosValue<Serializable>(Statics.BASE_POSITION, Boolean.TRUE), modified) });
				}
			};
		return snapshot;
	}
}