package org.genericsystem.framework.component;

import java.util.List;

import org.genericsystem.core.Generic;

public abstract class AbstractTypesGridComponent extends AbstractComponent {

	public AbstractTypesGridComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getGenerics().filter(new FilterGeneric<Generic>()).project(new ProjectorGeneric<AbstractComponent, Generic>());
	}

}
