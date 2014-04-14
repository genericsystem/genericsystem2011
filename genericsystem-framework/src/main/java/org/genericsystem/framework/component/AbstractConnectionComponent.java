package org.genericsystem.framework.component;

import java.util.Collections;
import java.util.List;

public abstract class AbstractConnectionComponent extends AbstractComponent {

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

	public abstract String connect();

	public abstract String disconnect();

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
}
