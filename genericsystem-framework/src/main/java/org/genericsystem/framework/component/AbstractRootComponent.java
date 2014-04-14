package org.genericsystem.framework.component;

import javax.inject.Inject;

import org.genericsystem.core.Cache;

public abstract class AbstractRootComponent extends AbstractComponent {
	@Inject
	private Cache cache;

	public AbstractRootComponent() {
		super(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent> T getRoot() {
		return (T) this;
	}

	@Override
	public Cache getCache() {
		return cache;
	}
}
