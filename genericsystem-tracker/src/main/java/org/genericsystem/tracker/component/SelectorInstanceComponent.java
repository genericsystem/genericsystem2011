package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.CreateAndEditComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;

public class SelectorInstanceComponent extends AbstractSelectorComponent {

	private Generic generic;

	public SelectorInstanceComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		this.generic = generic;
	}

	public Generic getGeneric() {
		return generic;
	}

	@Override
	public void select(Generic selected) {
		this.selected = selected;
		reInitChildren();
	}

	public Generic getSelected() {
		return selected;
	}

	@Override
	protected AbstractComponent initDisplayer() {
		child = new CreateAndEditComponent(this, selected);
		return child;
	}

	@Override
	protected AbstractComponent initChooser() {
		// return new ChooserCreateEditComponent(this, generic);
		return new TypeComponent(this, generic);
	}

}
