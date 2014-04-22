package org.genericsystem.tracker.component.generic;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

public class CommandButtonComponent extends AbstractGenericComponent implements Serializable {

	private static final long serialVersionUID = 1587708623458723053L;

	public CommandButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return Collections.emptyList();
	}

	public String select() {
		getParentSelector().select(getGeneric());
		return "index.xhtml";
	}

	@Override
	protected UIComponent buildJsfComponentsBefore(UIComponent father) {
		HtmlForm form1 = new HtmlForm();
		HtmlCommandButton commandButton = new HtmlCommandButton();
		commandButton.setValue(this);
		commandButton.setActionExpression(getMethodExpression("select"));
		form1.getChildren().add(commandButton);
		return form1;
	}
}
