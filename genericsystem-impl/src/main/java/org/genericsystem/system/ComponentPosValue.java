package org.genericsystem.system;

import java.io.Serializable;
import java.util.Objects;

public class ComponentPosValue<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -7566392327806983804L;

	private final int componentPos;

	private final T value;

	public ComponentPosValue(int componentPos, T value) {
		this.componentPos = componentPos;
		this.value = value;
	}

	public int getComponentPos() {
		return componentPos;
	}

	public T getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComponentPosValue))
			return false;
		@SuppressWarnings("unchecked")
		ComponentPosValue<Serializable> compare = (ComponentPosValue<Serializable>) obj;
		return compare.componentPos == componentPos && Objects.equals(compare.getValue(), value);
	}

	@Override
	public int hashCode() {
		return componentPos;
	}

	@Override
	public String toString() {
		return "(" + componentPos + "|" + value + ")";
	}

}
