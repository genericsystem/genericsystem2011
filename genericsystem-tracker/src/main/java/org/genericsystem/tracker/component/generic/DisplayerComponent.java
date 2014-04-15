package org.genericsystem.tracker.component.generic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.tracker.component.SelectorCreateEditComponent;

public class DisplayerComponent extends AbstractComponent {

	AbstractComponent child;

	public DisplayerComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return child == null ? Collections.emptyList() : Arrays.asList(child);
	}

	public void buildChild(Generic selected) {
		child = new SelectorCreateEditComponent(this, selected);
		children = null;
	}
}
