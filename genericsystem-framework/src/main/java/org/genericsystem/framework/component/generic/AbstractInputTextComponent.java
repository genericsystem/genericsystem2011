package org.genericsystem.framework.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractInputTextComponent extends AbstractGenericComponent {

	public AbstractInputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editInputText();
	}

	public abstract void editInputText();

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

}
