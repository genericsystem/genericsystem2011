package org.genericsystem.framework.component.generic;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractCollectableChildrenComponent extends AbstractGenericComponent {
	protected String newValue;

	public AbstractCollectableChildrenComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getGenerics().project(new ProjectorGeneric<AbstractComponent, Generic>());
		// return getGenerics().filter(new FilterGeneric<Generic>()).project(new ProjectorGeneric<AbstractComponent, Generic>());
	}

}
