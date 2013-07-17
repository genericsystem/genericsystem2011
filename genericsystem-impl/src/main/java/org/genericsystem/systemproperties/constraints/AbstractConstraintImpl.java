package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractProjectionIterator;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.genericsystem.systemproperties.constraints.Constraint.CheckingType;

public abstract class AbstractConstraintImpl extends GenericImpl {

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

	public abstract void check(Holder valueBaseComponent, AxedConstraintClass key) throws ConstraintViolationException;

	// @Override
	// public int compareTo(AbstractConstraintImpl otherConstraint) {
	// int result = Integer.valueOf(getPriority()).compareTo(Integer.valueOf(otherConstraint.getPriority()));
	// if (result != 0)
	// return result;
	// return this.getClass().getName().compareTo(otherConstraint.getClass().getName());
	// }

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
	protected static Snapshot<ConstraintValue> getConstraintValues(final Generic modified, final Class<? extends AbstractConstraintImpl> clazz) {
		return new AbstractSnapshot<ConstraintValue>() {
			@Override
			public Iterator<ConstraintValue> iterator() {
				// TODO base pos KK
				Iterator<ConstraintValue> iterator = new AbstractProjectionIterator<Holder, ConstraintValue>(((GenericImpl) modified).getHolders(((EngineImpl) modified.getEngine()).getCurrentCache().<Attribute> find(clazz), Statics.BASE_POSITION)
						.iterator()) {
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

	public AbstractConstraintImpl bindAxedConstraint(int pos) {
		Generic implicit = getEngine().bindPrimary(Generic.class, new AxedConstraintClass(getClass(), pos), SystemGeneric.STRUCTURAL, true);
		return getCurrentCache().<GenericImpl> find(MapInstance.class).bind(getClass(), implicit, this, getBasePos(this), false, new Generic[] {});
	}

	public <T extends AbstractConstraintImpl> T findAxedConstraint(int pos) {
		Generic implicit = getEngine().findPrimary(new AxedConstraintClass(getClass(), pos), SystemGeneric.STRUCTURAL);
		if (implicit == null)
			return null;
		return getCurrentCache().<GenericImpl> find(MapInstance.class).<T> find(implicit, this, getBasePos(this), new Generic[] {});
	}

	public static class AxedConstraintClass implements Serializable {
		private static final long serialVersionUID = 182492104604984855L;

		private final Class<?> clazz;
		private final int axe;

		public AxedConstraintClass(Class<?> clazz, int axe) {
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
			if (!(obj instanceof AxedConstraintClass))
				return false;
			AxedConstraintClass compare = (AxedConstraintClass) obj;
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