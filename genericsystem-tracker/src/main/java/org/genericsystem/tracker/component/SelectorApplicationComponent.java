package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class SelectorApplicationComponent extends AbstractSelectorComponent {

	private static final String ADMIN = "Administration";

	private static final String APPLI = "Application";

	public SelectorApplicationComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void select(Generic selected, String... value) {
		this.selected = selected;
		selected(value[0]);
		reInitChildren();
	}

	@Override
	protected AbstractComponent initDisplayer() {
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new ChooserApplicationComponent(this);
	}

	public void selected(String selected) {
		if (ADMIN.equals(selected))
			child = new SelectorAdministrationComponent(this);
		if (APPLI.equals(selected))
			child = new SelectorTypeComponent(this);
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
