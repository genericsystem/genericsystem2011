package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.SelectorEditComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types.Issues;

public class SelectorTypeComponent extends AbstractSelectorComponent {

	public SelectorTypeComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new PanelGridComponent(this));
	}

	public void selectDefaultComponent() {
		select(getCache().find(Issues.class));
	}

	@Override
	public void select(Generic selected) {
		// TODO KK
		SelectorEditComponent selectorTypeComponent = new SelectorEditComponent(this);
		selectorTypeComponent.getChildren().add(new TypeComponent(selectorTypeComponent, selected));
		this.child = selectorTypeComponent;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selectorType.xhtml";
	}
}
