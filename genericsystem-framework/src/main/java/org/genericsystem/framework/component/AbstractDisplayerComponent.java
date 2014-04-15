package org.genericsystem.framework.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;

public abstract class AbstractDisplayerComponent extends AbstractComponent {

	public AbstractDisplayerComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getChild() == null ? Collections.emptyList() : Arrays.asList(getChild());
	}

	protected AbstractComponent getChild() {
		return getParentSelector().getChild();
	}

	protected abstract AbstractComponent displayChild(Generic selected);

}
