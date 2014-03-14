package org.genericsystem.framework.component;

import java.util.List;

import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Type;

public abstract class AbstractTypesGridComponent extends AbstractComponent {

	public AbstractTypesGridComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new FilterGeneric<Type>()).project(new ProjectorGeneric<AbstractTypeComponent, Type>());
	}

}
