package org.genericsystem.tracker.component.generic;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public class RowComponent extends AbstractGenericComponent {

	public RowComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new OutputTextComponent(parent, getGeneric()), getGeneric().isRelation() ? new SelectItemComponent(parent, getGeneric()) : new InputTextComponent(parent, getGeneric()));
	}

	@Override
	public String getXhtmlPath() {
		return "row.xhtml";
	}
}
