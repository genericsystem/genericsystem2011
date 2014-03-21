package org.genericsystem.framework.component;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

public abstract class AbstractCollectableChildrenComponent extends AbstractGenericComponent {

	public AbstractCollectableChildrenComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getGenerics().filter(new FilterGeneric<Generic>()).project(new ProjectorGeneric<AbstractComponent, Generic>());
	}

}
