package org.genericsystem.tracker.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types;

public class GridComponent extends AbstractCollectableChildrenComponent {

	public GridComponent(AbstractComponent parent) {
		super(parent);
		// log.info("-------------------------->" + this.getThisExpression());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) getCache().getAllTypes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Serializable value = candidate.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && Types.class.equals(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U key) {
		return (T) new TypeComponent(GridComponent.this, key);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/grid.xhtml";
	}

}
