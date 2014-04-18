package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class SelectorTypeComponent extends AbstractSelectorComponent {

	public SelectorTypeComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public void select(Generic selected) {
		this.selected = selected;
		reInitChildren();
	}

	@Override
	protected AbstractComponent initDisplayer() {
		child = new SelectorInstanceComponent(this, selected);
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new ChooserComponent(this);
	}

}
