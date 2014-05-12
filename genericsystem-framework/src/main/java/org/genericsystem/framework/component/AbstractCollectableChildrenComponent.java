package org.genericsystem.framework.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;

public abstract class AbstractCollectableChildrenComponent extends AbstractComponent {

	private boolean isDirty = false;

	public AbstractCollectableChildrenComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public final List<? extends AbstractComponent> initChildren() {
		return getGenerics().filter(new FilterGeneric<Generic>()).project(new ProjectorGeneric<AbstractComponent, Generic>());
	}

	public abstract <T extends Generic> Snapshot<T> getGenerics();

	public abstract <T extends Generic> boolean isSelected(T candidate);

	public class FilterGeneric<T extends Generic> implements Filter<T> {

		@Override
		public boolean isSelected(T candidate) {
			return AbstractCollectableChildrenComponent.this.isSelected(candidate);
		}

	}

	public abstract <T extends AbstractComponent, U extends Generic> T buildComponent(U generic);

	public class ProjectorGeneric<T extends AbstractComponent, U extends Generic> implements Projector<T, U> {

		private final Map<U, T> map = new HashMap<U, T>() {

			private static final long serialVersionUID = -7927996818181180784L;

			@SuppressWarnings("unchecked")
			@Override
			public T get(Object key) {
				T result = super.get(key);
				if (result == null) {
					put((U) key, result = buildComponent((U) key));
					setDirty(true);
				}
				return result;
			}
		};

		@Override
		public T project(U element) {
			return map.get(element);
		}
	};

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

}
