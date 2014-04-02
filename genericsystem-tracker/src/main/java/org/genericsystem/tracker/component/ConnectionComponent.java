package org.genericsystem.tracker.component;

import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.security.manager.SecurityManager;

public class ConnectionComponent extends AbstractComponent implements SystemEventListener {

	private String login;

	private String password;

	public SecurityManager getSecurityManager() {
		return this.<RootComponent> getRoot().getSecurityManager();
	}

	public ConnectionComponent(AbstractComponent parent) {
		super(parent);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
	}

	public void connect() {
		getSecurityManager().connect(login, password);
	}

	public void disconnect() {
		getSecurityManager().disconnect();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/connection.xhtml";
	}

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

	@Override
	public boolean isListenerForSource(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processEvent(SystemEvent arg0) throws AbortProcessingException {
		// TODO Auto-generated method stub

	}

}
