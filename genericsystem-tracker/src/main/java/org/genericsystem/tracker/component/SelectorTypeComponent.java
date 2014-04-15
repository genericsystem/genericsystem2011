package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.DisplayerComponent;
import org.genericsystem.tracker.structure.Types.Issues;

public class SelectorTypeComponent extends AbstractSelectorComponent {

	public SelectorTypeComponent(AbstractComponent parent) {
		super(parent);
	}

	DisplayerComponent filterComponent = new DisplayerComponent(this);

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ChooserComponent(this), filterComponent);
	}

	public void selectDefaultComponent() {
		select(getCache().find(Issues.class));
	}

	@Override
	public void select(Generic selected) {
		filterComponent.buildChild(selected);
	}

	public DisplayerComponent getFilterComponent() {
		return filterComponent;
	}

	public void setFilterComponent(DisplayerComponent filterComponent) {
		this.filterComponent = filterComponent;
	}
}
