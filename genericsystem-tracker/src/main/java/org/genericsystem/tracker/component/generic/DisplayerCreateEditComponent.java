package org.genericsystem.tracker.component.generic;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractDisplayerComponent;

public class DisplayerCreateEditComponent extends AbstractDisplayerComponent {

	public DisplayerCreateEditComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public AbstractComponent displayChild(Generic selected) {
		children = null;
		return new CreateAndEditComponent(this, selected);
	}
}
