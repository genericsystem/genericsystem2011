package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;

public class InputTextComponent extends AbstractValuedGenericComponent implements ValuedComponent {

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		newValue = generic == null ? "" : Objects.toString(generic);
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
	protected UIComponent buildJsfComponentsAfter(UIComponent father) {
		HtmlInputText inputText = new HtmlInputText();
		inputText.setValueExpression("value", getValueExpression("newValue"));
		return inputText;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean isDirty) {
		return;
	}
}
