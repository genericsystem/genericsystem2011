package org.genericsystem.framework.component;

import java.util.Collections;
import java.util.List;

import javax.faces.event.SystemEventListener;

public abstract class AbstractConnectionComponent extends AbstractComponent implements SystemEventListener {

	private String login;

	private String password;

	public AbstractConnectionComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public abstract <T> T getSecurityManager();

	public abstract void connect();

	public abstract void disconnect();

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// @Override
	// protected String getThisExpression() {
	// return "" + getParent().getChildren().indexOf(this);
	// }
}
