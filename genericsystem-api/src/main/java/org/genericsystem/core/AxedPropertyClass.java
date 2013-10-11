package org.genericsystem.core;

import java.io.Serializable;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class AxedPropertyClass implements Serializable {

	private static final long serialVersionUID = -2631066712866842794L;

	private final Class<? extends Generic> clazz;
	private final int axe;

	public AxedPropertyClass(Class<? extends Generic> clazz, int axe) {
		this.clazz = clazz;
		this.axe = axe;
	}

	public Class<? extends Generic> getClazz() {
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
		return "class : " + clazz.getSimpleName() + ", axe : " + axe;
	}
}
