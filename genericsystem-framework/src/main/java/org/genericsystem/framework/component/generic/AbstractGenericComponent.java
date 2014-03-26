package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractGenericComponent extends AbstractComponent implements GenericComponent {

	protected Generic generic;

	public AbstractGenericComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		this.generic = generic;
		this.children = initChildren();
	}

	@Override
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

	// public String getColumnTitleAttribute() {
	// if (!isRelation())
	// return Objects.toString(getGeneric());
	// else
	// return Objects.toString((this.<AbstractGenericComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	// }

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

}
