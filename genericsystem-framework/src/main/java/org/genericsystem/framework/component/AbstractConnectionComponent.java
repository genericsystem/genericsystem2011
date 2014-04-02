package org.genericsystem.framework.component;

import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEventListener;

public abstract class AbstractConnectionComponent extends AbstractComponent implements SystemEventListener {

	private String login;

	private String password;

	public AbstractConnectionComponent(AbstractComponent parent) {
		super(parent);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
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
}
