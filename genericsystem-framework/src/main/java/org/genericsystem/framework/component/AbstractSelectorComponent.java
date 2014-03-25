package org.genericsystem.framework.component;

import org.genericsystem.core.Generic;

public abstract class AbstractSelectorComponent extends AbstractCollectableChildrenComponent {
	private AbstractComponent child;

	public AbstractSelectorComponent(AbstractComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	public abstract void selectDefaultComponent();

	public abstract void select(Generic selected);

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getChild() {
		return (T) child;
	}

	public void setChild(AbstractComponent child) {
		this.child = child;
	}

}
