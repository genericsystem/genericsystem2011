package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.core.GenericImpl;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

public abstract class AbstractAxedConstraintImpl extends AbstractConstraintImpl {

	private static final long serialVersionUID = 6417651505947151178L;

	public Serializable bindAxedConstraint(int pos) {
		AxedConstraintClass key = new AxedConstraintClass(getClass(), pos);
		getCurrentCache().<GenericImpl> find(MapInstance.class).setSubAttribute(this, key);
		return key;
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