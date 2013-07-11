package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

public abstract class AbstractAxedConstraintImpl extends AbstractConstraintImpl {

	private static final long serialVersionUID = 6417651505947151178L;

	public AxedConstraintClass bindAxedConstraint(int pos) {
		@SuppressWarnings("unchecked")
		AxedConstraintClass key = new AxedConstraintClass((Class<Serializable>) getClass(), pos);
		// getCurrentCache().<GenericImpl> find(MapInstance.class).setSubAttribute(this, key);

		getCurrentCache().<GenericImpl> find(MapInstance.class).bind(getEngine().bindPrimary(getClass(), key, SystemGeneric.STRUCTURAL, true), this, getBasePos(this), false, new Generic[] {});
		return key;
	}

	@SuppressWarnings("unchecked")
	public AbstractAxedConstraintImpl findConstraint(int pos) {
		AxedConstraintClass key = new AxedConstraintClass((Class<Serializable>) getClass(), pos);
		// getCurrentCache().<GenericImpl> find(MapInstance.class).setSubAttribute(this, key);
		log.info("soluce " + getClass());
		MapInstance map = getCurrentCache().<MapInstance> find(MapInstance.class);

		return map.bind(getEngine().bindPrimary(getClass(), key, SystemGeneric.STRUCTURAL, true), this, getBasePos(this), false, new Generic[] {});

		// Generic implicit = findPrimary(new AxedConstraintClass((Class<Serializable>) getClass(), pos), SystemGeneric.STRUCTURAL);
		// log.info("implicit " + implicit + " " + getClass());
		// if (implicit == null)
		// return null;
		// return getCurrentCache().<GenericImpl> find(MapInstance.class).<AbstractAxedConstraintImpl> find(implicit, this, getBasePos(this), new Generic[] {});
	}

	public abstract void check(Generic baseComponent, Generic modified, int axe) throws ConstraintViolationException;

	public static class AxedConstraintClass implements Serializable {
		private static final long serialVersionUID = 182492104604984855L;

		private final Class<Serializable> clazz;
		private final int axe;

		public AxedConstraintClass(Class<Serializable> clazz, int axe) {
			this.clazz = clazz;
			this.axe = axe;
		}

		public Class<Serializable> getClazz() {
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