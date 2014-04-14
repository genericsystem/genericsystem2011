package org.genericsystem.framework.component;

import org.genericsystem.core.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSelectorComponent extends AbstractComponent {

	protected static Logger log = LoggerFactory.getLogger(AbstractSelectorComponent.class);

	protected AbstractComponent child;

	public AbstractSelectorComponent(AbstractComponent parent) {
		super(parent);
		// selectDefaultComponent();
	}

	public final AbstractSelectorComponent getParentSelector() {
		return this;
	}

	public abstract void selectDefaultComponent();

	public abstract void select(Generic selected);

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getChild() {
		return (T) child;
	}

}
