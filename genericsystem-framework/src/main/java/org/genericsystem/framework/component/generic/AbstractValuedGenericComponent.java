package org.genericsystem.framework.component.generic;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;

public abstract class AbstractValuedGenericComponent extends AbstractGenericComponent implements ValuedComponent {

	public AbstractValuedGenericComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	private String newValue;

	@Override
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
