package org.genericsystem.jsf.example.component;

import java.util.List;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponent {
	protected static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	protected AbstractComponent parent;
	protected List<? extends AbstractComponent> children;
	protected AbstractComponent child;

	public AbstractComponent() {
		this(null);
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

	public AbstractComponent getChild() {
		return child;
	}

}
