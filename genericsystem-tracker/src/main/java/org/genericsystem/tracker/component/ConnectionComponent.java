package org.genericsystem.tracker.component;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractConnectionComponent;
import org.genericsystem.security.manager.SecurityManager;

public class ConnectionComponent extends AbstractConnectionComponent {

	public ConnectionComponent(AbstractComponent parent) {
		super(parent);
		// log.info("-------------------------->" + this.getThisExpression());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSecurityManager() {
		return (T) this.<RootComponent> getRoot().getSecurityManager();
	}

	@Override
	public void connect() {

		((SecurityManager) getSecurityManager()).connect(getLogin(), getPassword());
	}

	@Override
	public void disconnect() {
		((SecurityManager) getSecurityManager()).disconnect();
	}

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isListenerForSource(Object source) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/connection.xhtml";
	}

	@Override
	protected void buildJsfComponentsAfter(UIComponent father) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HtmlForm form = new HtmlForm();
		HtmlOutputText outputLogin = new HtmlOutputText();
		outputLogin.setValue("Login :");
		HtmlInputText inputLogin = new HtmlInputText();

		// ctx.getELContext().getVariableMapper().setVariable(variable, expression);
		inputLogin.setValueExpression("login", ctx.getApplication().getExpressionFactory().createValueExpression("#{" + inputLogin.getId() + ".login}", String.class));
		HtmlOutputText outputPassword = new HtmlOutputText();
		outputPassword.setValue("Password :");
		HtmlInputText inputPassword = new HtmlInputText();
		HtmlCommandButton buttonSubmit = new HtmlCommandButton();
		buttonSubmit.setValue("connect");

		ctx.getViewRoot().addComponentResource(ctx, form);
		ctx.getViewRoot().addComponentResource(ctx, outputLogin);
		ctx.getViewRoot().addComponentResource(ctx, inputLogin);
		ctx.getViewRoot().addComponentResource(ctx, outputPassword);
		ctx.getViewRoot().addComponentResource(ctx, inputPassword);
		ctx.getViewRoot().addComponentResource(ctx, buttonSubmit);
	}

}
