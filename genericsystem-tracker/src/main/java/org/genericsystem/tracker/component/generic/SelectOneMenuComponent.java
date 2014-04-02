package org.genericsystem.tracker.component.generic;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;

public class SelectOneMenuComponent extends AbstractValuedGenericComponent implements ValuedComponent {

	public SelectOneMenuComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return new ArrayList<>(); // TODO KK cf RowComponent::initChildren
	}

	@Override
	public String getXhtmlPath() {
		return "selectOneMenu.xhtml";
	}
}