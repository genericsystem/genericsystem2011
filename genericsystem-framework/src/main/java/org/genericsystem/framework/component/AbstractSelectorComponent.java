package org.genericsystem.framework.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSelectorComponent extends AbstractComponent {

	protected static Logger log = LoggerFactory.getLogger(AbstractSelectorComponent.class);

	public AbstractComponent child;

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

	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(initChooser(), initDisplayer());
	}

	protected abstract AbstractComponent initDisplayer();

	protected abstract AbstractComponent initChooser();

}
