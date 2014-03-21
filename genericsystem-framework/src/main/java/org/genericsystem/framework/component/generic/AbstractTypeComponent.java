package org.genericsystem.framework.component.generic;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractTypeComponent extends AbstractValueAndGenericComponent {

	public AbstractTypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getGenerics().project(new ProjectorGeneric<AbstractComponent, Generic>());
	}
}
