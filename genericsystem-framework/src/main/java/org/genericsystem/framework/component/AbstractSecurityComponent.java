package org.genericsystem.framework.component;


public abstract class AbstractSecurityComponent extends AbstractComponent {

	protected AbstractComponent child;

	public AbstractSecurityComponent(AbstractComponent parent) {
		this.parent = parent;
	}

	protected abstract AbstractComponent initApplication();
}
