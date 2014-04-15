package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.DisplayerCreateEditComponent;

public class SelectorCreateEditComponent extends AbstractSelectorComponent {

	Generic selected;

	public SelectorCreateEditComponent(AbstractComponent parent, Generic selected) {
		super(parent);
		this.selected = selected;
	}

	DisplayerCreateEditComponent displayerCreateEdit = new DisplayerCreateEditComponent(this);

	@Override
	public void selectDefaultComponent() {

	}

	@Override
	public void select(Generic selected) {
		child = displayerCreateEdit.buildChild(selected);
	}

	public Generic getSelected() {
		return selected;
	}

	@Override
	protected AbstractComponent initDisplayer() {
		return displayerCreateEdit;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new ChooserCreateEditComponent(this);
	}

}
