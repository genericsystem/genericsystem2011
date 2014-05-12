package org.genericsystem.tracker.component;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;

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
	public CashManagementComponent getCashManagement() {
		return this.<RootComponent> getRoot().getCashManagement();
	}

	@Override
	public String connect() {
		((SecurityManager) getSecurityManager()).connect(getLogin(), getPassword());
		getRoot().reInitChildren();
		return INDEX_XHTML;
	}

	@Override
	public String disconnect() {
		((SecurityManager) getSecurityManager()).disconnect();
		getRoot().reInitChildren();
		return INDEX_XHTML;
	}

	public String save() {
		getCashManagement().flushCurrentCache();
		return INDEX_XHTML;
	}

	public String mountCache() {
		getCashManagement().mountNewCache();
		return INDEX_XHTML;
	}

	public String discardCache() {
		getCashManagement().discardCurrentCache();
		return INDEX_XHTML;
	}

	public String getCacheLevel() {
		return " Level : " + getCashManagement().getLevelCasheProvider().toString();
	}

	@Override
	protected UIComponent buildJsfComponentsBefore(UIComponent father) {
		HtmlForm formAuthentification = new HtmlForm();
		if (!((SecurityManager) getSecurityManager()).isConnected()) {

			HtmlOutputText outputLogin = new HtmlOutputText();
			outputLogin.setValue("Login");

			HtmlInputText inputLogin = new HtmlInputText();
			inputLogin.setValueExpression("value", getValueExpression("login"));

			HtmlOutputText outputPassword = new HtmlOutputText();
			outputPassword.setValue("Password");

			HtmlInputSecret inputPassword = new HtmlInputSecret();
			inputPassword.setValueExpression("value", getValueExpression("password"));

			HtmlCommandButton buttonSubmit = new HtmlCommandButton();
			buttonSubmit.setValue("connect");
			buttonSubmit.setActionExpression(getMethodExpression("connect"));

			formAuthentification.getChildren().add(outputLogin);
			formAuthentification.getChildren().add(inputLogin);
			formAuthentification.getChildren().add(outputPassword);
			formAuthentification.getChildren().add(inputPassword);
			formAuthentification.getChildren().add(buttonSubmit);

			return formAuthentification;
		}

		HtmlCommandButton buttonDisconnect = new HtmlCommandButton();
		buttonDisconnect.setValue("disconnect");
		buttonDisconnect.setActionExpression(getMethodExpression("disconnect"));

		HtmlCommandButton buttonSave = new HtmlCommandButton();
		buttonSave.setValue("save");
		buttonSave.setActionExpression(getMethodExpression("save"));

		HtmlCommandButton buttonMountCache = new HtmlCommandButton();
		buttonMountCache.setValue("mount cache");
		buttonMountCache.setActionExpression(getMethodExpression("mountCache"));

		HtmlCommandButton buttonDiscardCache = new HtmlCommandButton();
		buttonDiscardCache.setValue("discard cache");
		buttonDiscardCache.setActionExpression(getMethodExpression("discardCache"));

		HtmlOutputText cacheLevel = new HtmlOutputText();
		cacheLevel.setValueExpression("value", getValueExpression("getCacheLevel()"));

		formAuthentification.getChildren().add(buttonDisconnect);
		formAuthentification.getChildren().add(buttonSave);
		formAuthentification.getChildren().add(buttonMountCache);
		formAuthentification.getChildren().add(buttonDiscardCache);
		formAuthentification.getChildren().add(cacheLevel);

		return formAuthentification;
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
