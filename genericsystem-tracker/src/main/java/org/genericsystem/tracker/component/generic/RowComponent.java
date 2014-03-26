package org.genericsystem.tracker.component.generic;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class RowComponent extends AbstractGenericComponent {

	public RowComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new OutputTextComponent(parent, getGeneric()), getGeneric().isRelation() ? new SelectItemComponent(parent, getGeneric()) : new InputTextComponent(parent, getGeneric()));
	}

	@Override
	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<AbstractGenericComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public String getXhtmlPath() {
		return "row.xhtml";
	}
}
