package org.genericsystem.tracker.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractChooserComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types;

public class ChooserCreateEditComponent extends AbstractChooserComponent {

	public ChooserCreateEditComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public <T> T getSecurityManager() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) getCache().getAllTypes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Generic selected = ((SelectorCreateEditComponent) getParentSelector()).getSelected();
		Serializable value = candidate.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && Types.class.equals(clazz) && candidate.equals(selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		log.info("---*-*-*-*-*-*-**--*-*-*-*" + generic);
		return (T) new TypeComponent(ChooserCreateEditComponent.this, generic);

	}

}
