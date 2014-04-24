package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.CreateEditAdministrationComponent;
import org.genericsystem.tracker.component.generic.TypeAdministrationComponent;

public class SelectorInstanceAdministrationComponent extends AbstractSelectorComponent {

	public SelectorInstanceAdministrationComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void select(Generic selected, String... value) {
		this.selected = selected;
		reInitChildren();
	}

	@Override
	protected AbstractComponent initDisplayer() {
		child = new CreateEditAdministrationComponent(this, selected);
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new TypeAdministrationComponent(this, getTypeSelected());
	}
}
