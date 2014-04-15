package org.genericsystem.tracker.component.generic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public class DisplayerCreateEditComponent extends AbstractComponent {

	AbstractComponent child;

	public DisplayerCreateEditComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return child == null ? Collections.emptyList() : Arrays.asList(child);
	}

	public AbstractComponent buildChild(Generic selected) {
		child = new CreateAndEditComponent(this, selected);
		children = null;
		return child;
	}
}
