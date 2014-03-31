package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types.Issues;

public class SelectorComponent extends AbstractSelectorComponent {

	public SelectorComponent(AbstractComponent parent) {
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
		this.child = new TypeComponent(this, selected);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selector.xhtml";
	}
}
