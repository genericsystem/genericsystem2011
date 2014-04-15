package org.genericsystem.tracker.component.generic;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractDisplayerComponent;
import org.genericsystem.tracker.component.SelectorCreateEditComponent;

public class DisplayerComponent extends AbstractDisplayerComponent {

	public DisplayerComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public AbstractComponent displayChild(Generic selected) {
		children = null;
		return new SelectorCreateEditComponent(this, selected);
	}
}
