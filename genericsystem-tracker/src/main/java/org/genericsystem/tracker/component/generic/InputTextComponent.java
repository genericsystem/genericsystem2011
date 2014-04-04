package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;

public class InputTextComponent extends AbstractValuedGenericComponent implements ValuedComponent {

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		newValue = Objects.toString(generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	public void setNewValue(String newValue) {
		super.setNewValue(newValue);
	}

	@Override
	public String getXhtmlPath() {
		return "inputText.xhtml";
	}

}
