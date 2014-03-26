package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractGenericComponent extends AbstractComponent {

	protected Generic generic;

	public AbstractGenericComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		this.generic = generic;
		this.children = initChildren();
	}

	public Generic getGeneric() {
		return generic;
	}

	public void setGeneric(Generic generic) {
		this.generic = generic;
	}

	@Override
	public String toString() {
		return Objects.toString(generic);
	}

	public abstract String getColumnTitleAttribute();

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

}
