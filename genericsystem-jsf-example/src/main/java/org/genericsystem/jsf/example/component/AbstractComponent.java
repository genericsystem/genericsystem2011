package org.genericsystem.jsf.example.component;

import java.util.List;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponent {
	protected static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	private final AbstractComponent parent;
	protected List<? extends AbstractComponent> children;

	public AbstractComponent() {
		this.parent = null;
	}

	public AbstractComponent(AbstractComponent parent) {
		this.parent = parent;
	}

	public abstract List<? extends AbstractComponent> initChildren();

	public abstract String getXhtmlPath();

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getParent() {
		return (T) parent;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> List<T> getChildren() {
		return (List<T>) children;
	}

	public RootComponent getRoot() {
		return getParent().getRoot();
	}

	public Cache getCache() {
		return getRoot().getCache();
	}
}
