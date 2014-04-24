package org.genericsystem.tracker.component;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.genericsystem.framework.component.AbstractComponent;

public class ErrorComponent extends AbstractComponent {

	public ErrorComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	protected UIComponent buildJsfComponentsAfter(UIComponent father) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setValue("error ....");
		ctx.getViewRoot().getChildren().add(outputText);
		return outputText;
	}
}
