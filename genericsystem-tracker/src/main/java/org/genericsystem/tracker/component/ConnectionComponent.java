package org.genericsystem.tracker.component;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;

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
}
