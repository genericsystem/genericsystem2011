package org.genericsystem.framework.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractTextAreaComponent extends AbstractGenericComponent {

	public AbstractTextAreaComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editTextArea();
	}

	public abstract void editTextArea();

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}
}
