package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

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

// public abstract class AbstractGenericComponent extends AbstractComponent {
// private final Generic generic;
//
// public AbstractGenericComponent(AbstractComponent parent, Generic generic) {
// super(parent);
// this.generic = generic;
// this.children = initChildren();
// }
//
// public Generic getGeneric() {
// return generic;
// }
//
// @Override
// public String toString() {
// return Objects.toString(generic);
// }
// }
