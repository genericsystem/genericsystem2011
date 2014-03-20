package org.genericsystem.framework.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
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

	public abstract List<? extends AbstractComponent> initChildren();

	public abstract <T extends Generic> Snapshot<T> getGenerics();

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

	public abstract <T extends AbstractComponent, U extends Generic> T buildComponent(U generic);

	public class ProjectorGeneric<T extends AbstractComponent, U extends Generic> implements Projector<T, U> {
		private final Map<U, T> map = new HashMap<U, T>() {

			private static final long serialVersionUID = -7927996818181180784L;

			@SuppressWarnings("unchecked")
			@Override
			public T get(Object key) {
				T result = super.get(key);
				if (result == null)
					put((U) key, result = buildComponent((U) key));
				return result;
			}
		};

		@Override
		public T project(U element) {
			return map.get(element);
		}
	};

	public abstract <T extends Generic> boolean isSelected(T candidate);

	public class FilterGeneric<T extends Generic> implements Filter<T> {

		@Override
		public boolean isSelected(T candidate) {
			return AbstractComponent.this.isSelected(candidate);
		}

	}
}
