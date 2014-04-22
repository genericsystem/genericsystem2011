package org.genericsystem.jsf.example.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.jsf.example.structure.Types;

public class TypesGridComponent extends AbstractCollectableChildrenComponent {
	public TypesGridComponent(AbstractComponent parent) {
		super(parent);
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
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new TypeComponent(this, generic);
	}

	public String getXhtmlPath() {
		return "/pages/typesgrid.xhtml";
	}

	@Override
	public <T> T getSecurityManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
