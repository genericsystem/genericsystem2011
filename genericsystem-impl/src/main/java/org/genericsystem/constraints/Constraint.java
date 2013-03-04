package org.genericsystem.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.AbstractConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractProjectorAndFilterIterator;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.genericsystem.system.ComponentPosValue;

public abstract class Constraint implements Comparable<Constraint>, Serializable {

	private static final long serialVersionUID = -3257819220762195050L;

	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}

	public abstract void check(Context context, Generic modified) throws AbstractConstraintViolationException;

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

		private ComponentPosValue<Serializable> componentPosValue;

		private Generic constraintType;

		public ConstraintValue(ComponentPosValue<Serializable> componentPosValue, Generic constraintType) {
			this.componentPosValue = componentPosValue;
			this.constraintType = constraintType;
		}

		public ComponentPosValue<Serializable> getComponentPosValue() {
			return componentPosValue;
		}

		public Generic getConstraintType() {
			return constraintType;
		}

	}

	// TODO it's clean ?
	protected Snapshot<ConstraintValue> getConstraintValues(final Context context, final Generic modified, final Class<? extends Constraint> clazz) {
		return new AbstractSnapshot<ConstraintValue>() {
			@Override
			public Iterator<ConstraintValue> iterator() {
				// TODO base pos KK
				Iterator<ConstraintValue> iterator = new AbstractProjectorAndFilterIterator<Holder, ConstraintValue>(modified.getHolders(context, ((AbstractContext) context).<Attribute> find(clazz), Statics.BASE_POSITION).iterator()) {
					@Override
					public boolean isSelected() {
						return !Boolean.FALSE.equals(next.<ComponentPosValue<Serializable>> getValue().getValue());
					}

					@Override
					protected ConstraintValue project() {
						// TODO KK
						return new ConstraintValue(next.<ComponentPosValue<Serializable>> getValue(), next.getBaseComponent());
					}
				};
				if (!iterator.hasNext() && clazz.getAnnotation(SystemGeneric.class).defaultBehavior()) {
					List<ConstraintValue> constraintValues = new ArrayList<>(modified.getComponentsSize());
					for (int i = 0; i < modified.getComponentsSize(); i++)
						constraintValues.add(new ConstraintValue(new ComponentPosValue<Serializable>(i, Boolean.TRUE), modified));
					return constraintValues.iterator();
				}
				return iterator;
			}
		};
	}
}