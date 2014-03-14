package org.genericsystem.framework.component;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.generic.AbstractButtonComponent;
import org.genericsystem.generic.Type;

public abstract class AbstractSelectorComponent extends AbstractComponent {

	private AbstractComponent child;

	public AbstractSelectorComponent(AbstractComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new FilterGeneric<Type>()).project(new ProjectorGeneric<AbstractButtonComponent, Type>());
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
