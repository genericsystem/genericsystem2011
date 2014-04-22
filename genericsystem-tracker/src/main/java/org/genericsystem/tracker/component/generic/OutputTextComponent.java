package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;

public class OutputTextComponent extends AbstractValuedGenericComponent implements ValuedComponent {

	public OutputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		newValue = Objects.toString(generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	protected UIComponent buildJsfComponentsBefore(UIComponent father) {
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setValueExpression("value", getValueExpression("newValue"));
		return outputText;
	}
}
