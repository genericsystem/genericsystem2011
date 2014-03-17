package org.genericsystem.framework.component;


public abstract class AbstractRootComponent extends AbstractComponent {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent> T getRoot() {
		return (T) this;
	}

}
