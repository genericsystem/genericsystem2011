package org.genericsystem.tracker.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.DisplayerComponent;
import org.genericsystem.tracker.structure.Types.Issues;

public class SelectorTypeComponent extends AbstractSelectorComponent {

	public SelectorTypeComponent(AbstractComponent parent) {
		super(parent);
	}

	DisplayerComponent displayerComponent = new DisplayerComponent(this);

	// @Override
	// public List<? extends AbstractComponent> initChildren() {
	// return Arrays.asList(new ChooserComponent(this), displayerComponent);
	// }

	public void selectDefaultComponent() {
		select(getCache().find(Issues.class));
	}

	@Override
	public void select(Generic selected) {
		displayerComponent.buildChild(selected);
	}

	@Override
	protected AbstractComponent initDisplayer() {
		return displayerComponent;
	}

	@Override
	protected AbstractComponent initChooser() {
		return new ChooserComponent(this);
	}

}
