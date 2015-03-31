package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class SelectorAdministrationComponent extends AbstractSelectorComponent {

	public SelectorAdministrationComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void select(Generic selected, String... value) {
		this.selected = selected;
		reInitChildren();
	}

	@Override
	protected AbstractComponent initDisplayer() {
		child = new SelectorInstanceAdministrationComponent(this);
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new ChooserAdministrationComponent(this);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean isDirty) {
		return;

	}

}
