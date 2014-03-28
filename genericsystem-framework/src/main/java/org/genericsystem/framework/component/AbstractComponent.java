package org.genericsystem.framework.component;

import java.util.List;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponent {
	protected static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	protected AbstractComponent parent;
	protected List<? extends AbstractComponent> children;

	public AbstractComponent() {
		this(null);
	}

	public AbstractComponent(AbstractComponent parent) {
		this.parent = parent;
	}

	public AbstractSelectorComponent getParentSelector() {
		return getParent().getParentSelector();
	}

	public abstract List<? extends AbstractComponent> initChildren();

	public abstract String getXhtmlPath();

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getParent() {
		return (T) parent;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> List<T> getChildren() {
		if (children == null)
			children = initChildren();
		return (List<T>) children;
	}

	public <T extends AbstractComponent> T getRoot() {
		return getParent().getRoot();
	}

	public Cache getCache() {
		return getRoot().getCache();
	}
}
