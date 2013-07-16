package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

public abstract class AbstractAxedConstraintImpl extends AbstractConstraintImpl {

	public AbstractAxedConstraintImpl bindAxedConstraint(int pos) {
		Generic implicit = getEngine().bindPrimary(Generic.class, new AxedConstraintClass(getClass(), pos), SystemGeneric.STRUCTURAL, true);
		return getCurrentCache().<GenericImpl> find(MapInstance.class).bind(getClass(), implicit, this, getBasePos(this), false, new Generic[] {});
	}

	public AbstractAxedConstraintImpl findAxedConstraint(int pos) {
		Generic implicit = getEngine().findPrimary(new AxedConstraintClass(getClass(), pos), SystemGeneric.STRUCTURAL);
		if (implicit == null)
			return null;
		return getCurrentCache().<GenericImpl> find(MapInstance.class).<AbstractAxedConstraintImpl> find(implicit, this, getBasePos(this), new Generic[] {});
	}

	@Override
	public void check(Holder valueBaseComponent, Serializable key, Class<? extends Serializable> keyClazz) throws ConstraintViolationException {
		if (key instanceof AxedConstraintClass) {
			AbstractAxedConstraintImpl constraint = findAxedConstraint(((AxedConstraintClass) key).getAxe());
			Generic baseComponent = valueBaseComponent != null ? valueBaseComponent.<Attribute> getBaseComponent().getBaseComponent() : null;
			if (isBooleanConstraintEnabledOrNotBoolean(valueBaseComponent, keyClazz))
				for (Generic inheriting : ((GenericImpl) baseComponent).getAllInheritings())
					constraint.check(baseComponent, inheriting, ((AxedConstraintClass) key).getAxe());
		}
	}

	public abstract void check(Generic baseComponent, Generic modified, int axe) throws ConstraintViolationException;

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