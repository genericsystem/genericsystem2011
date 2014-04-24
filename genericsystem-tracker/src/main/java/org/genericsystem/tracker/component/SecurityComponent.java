package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSecurityComponent;
import org.genericsystem.security.manager.SecurityManager;

public class SecurityComponent extends AbstractSecurityComponent {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSecurityManager() {
		return (T) this.<RootComponent> getRoot().getSecurityManager();
	}

	public SecurityComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		if (((SecurityManager) getSecurityManager()).getCurrentUser() == null)
			return Collections.emptyList();
		return Arrays.asList(initApplication());
	}

	@Override
	protected AbstractComponent initApplication() {
		if (((SecurityManager) getSecurityManager()).isAdmin())
			return child = new SelectorApplicationComponent(this);
		if (((SecurityManager) getSecurityManager()).isConnected())
			return child = new SelectorTypeComponent(this);
		return child = new ErrorComponent(this);
	}
}
