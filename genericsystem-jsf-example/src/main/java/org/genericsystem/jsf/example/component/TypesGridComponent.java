package org.genericsystem.jsf.example.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractTypesGridComponent;
import org.genericsystem.jsf.example.structure.Types;

public class TypesGridComponent extends AbstractTypesGridComponent {

	public TypesGridComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/typesgrid.xhtml";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new TypeComponent(this, generic);
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
}
