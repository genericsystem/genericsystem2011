package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.CreateAndEditComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;

public class SelectorInstanceComponent extends AbstractSelectorComponent {

	public SelectorInstanceComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void select(Generic selected, String... value) {
		this.selected = selected;
		reInitChildren();
	}

	@Override
	protected AbstractComponent initDisplayer() {
		child = new CreateAndEditComponent(this, selected);
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new TypeComponent(this, getTypeSelected());
	}
}
