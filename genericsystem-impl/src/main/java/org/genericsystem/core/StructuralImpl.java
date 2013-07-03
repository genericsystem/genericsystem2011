package org.genericsystem.core;

import org.genericsystem.generic.Attribute;

/**
 * @author Nicolas Feybesse
 * 
 */
public class StructuralImpl implements Structural {
	private final Attribute attribute;
	private final int position;

	public StructuralImpl(Attribute attribute, int position) {
		this.attribute = attribute;
		this.position = position;
	}

	@Override
	public Attribute getAttribute() {
		return attribute;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "AttributeWrapper [attribute=" + attribute + ", position=" + position + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof StructuralImpl))
			return false;
		StructuralImpl structural = (StructuralImpl) o;
		return attribute.equals(structural.attribute) && position == structural.position;
	}

	@Override
	public int hashCode() {
		return attribute.hashCode();
	}
}
