package org.genericsystem.tracker.component.generic;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class SelectorEditComponent extends AbstractSelectorComponent {

	public SelectorEditComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void selectDefaultComponent() {
		child = null;
	}

	@Override
	public void select(Generic selected) {
		// TODO KK
		child = new CreateAndEditComponent(this, selected);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return new ArrayList<>(); // TODO KK cf SelectorComponent::select
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selectorEdit.xhtml";
	}
}
