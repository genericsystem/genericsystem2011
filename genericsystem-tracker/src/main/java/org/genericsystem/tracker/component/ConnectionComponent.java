package org.genericsystem.tracker.component;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractConnectionComponent;
import org.genericsystem.security.manager.SecurityManager;

public class ConnectionComponent extends AbstractConnectionComponent {

	public ConnectionComponent(AbstractComponent parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSecurityManager() {
		return (T) this.<RootComponent> getRoot().getSecurityManager();
	}

	@Override
	public String connect() {
		((SecurityManager) getSecurityManager()).connect(getLogin(), getPassword());
		return "index.xhtml";
	}

	@Override
	public String disconnect() {
		((SecurityManager) getSecurityManager()).disconnect();
		return "index.xhtml";
	}

	@Override
	protected void buildJsfComponentsBefore(UIComponent father) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (!((SecurityManager) getSecurityManager()).isConnected()) {

			HtmlForm form1 = new HtmlForm();

			HtmlOutputText outputLogin = new HtmlOutputText();
			outputLogin.setValue("Login");

			HtmlInputText inputLogin = new HtmlInputText();
			inputLogin.setValueExpression("value", getValueExpression("login"));

			HtmlOutputText outputPassword = new HtmlOutputText();
			outputPassword.setValue("Password");

			HtmlInputText inputPassword = new HtmlInputText();
			inputPassword.setValueExpression("value", getValueExpression("password"));

			HtmlCommandButton buttonSubmit = new HtmlCommandButton();
			buttonSubmit.setValue("connect");

			buttonSubmit.setActionExpression(getMethodExpression("connect"));

			form1.getChildren().add(outputLogin);
			form1.getChildren().add(inputLogin);
			form1.getChildren().add(outputPassword);
			form1.getChildren().add(inputPassword);
			form1.getChildren().add(buttonSubmit);
			ctx.getViewRoot().getChildren().add(form1);
		} else {
			HtmlForm form = new HtmlForm();
			HtmlCommandButton buttonDisconnect = new HtmlCommandButton();
			buttonDisconnect.setValue("disconnect");
			buttonDisconnect.setActionExpression(getMethodExpression("disconnect"));
			form.getChildren().add(buttonDisconnect);
			ctx.getViewRoot().getChildren().add(form);
		}
	}

	@Override
	public String getXhtmlPath() {
		return null;
	}

}
