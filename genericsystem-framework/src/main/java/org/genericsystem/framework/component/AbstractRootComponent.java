package org.genericsystem.framework.component;

import javax.faces.component.UIViewRoot;
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

	protected void buildJsf(UIViewRoot root) {
		root.getChildren().add(buildJsfChildren(root));
	}

	@Override
	public Cache getCache() {
		return cache;
	}
}
