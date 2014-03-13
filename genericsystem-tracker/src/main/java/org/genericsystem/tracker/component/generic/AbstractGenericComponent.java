package org.genericsystem.tracker.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.component.AbstractComponent;

public abstract class AbstractGenericComponent extends AbstractComponent {

	private Generic generic;

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

	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<AbstractGenericComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

}
