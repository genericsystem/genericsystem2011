package org.genericsystem.tracker.component;

import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.security.manager.SecurityManager;

public class ChooserApplicationComponent extends AbstractComponent {

	public ChooserApplicationComponent(AbstractComponent parent) {
		this.parent = parent;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public String select(String value) {
		getParentSelector().select(((SecurityManager) getSecurityManager()).getCurrentUser(), value);
		return INDEX_XHTML;
	}

	@Override
	protected UIComponent buildJsfComponentsBefore(UIComponent container) {
		HtmlForm form = new HtmlForm();
		HtmlCommandButton buttonAdministration = new HtmlCommandButton();
		buttonAdministration.setValue("Administration");

		HtmlCommandButton buttonApplication = new HtmlCommandButton();
		buttonApplication.setValue("Application");

		buttonAdministration.setActionExpression(getMethodExpression("select('Administration')"));
		buttonApplication.setActionExpression(getMethodExpression("select('Application')"));
		form.getChildren().add(buttonApplication);
		form.getChildren().add(buttonAdministration);
		return form;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSecurityManager() {
		return (T) this.<RootComponent> getRoot().getSecurityManager();
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
