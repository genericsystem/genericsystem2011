package org.genericsystem.framework.component.generic;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractValueAndGenericComponent extends AbstractGenericComponent {

	protected String newValue;

	public AbstractValueAndGenericComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

}
