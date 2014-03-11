package org.genericsystem.jsf.example.component;

import java.io.Serializable;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractGridComponent;
import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Types;

public class GridComponent extends AbstractGridComponent {

	public GridComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public boolean isSelected(Type type) {
		Serializable value = type.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && Types.class.equals(clazz);
	}

	@Override
	public AbstractTypeComponent buildComponent(Type type) {
		return new TypeComponent(this, type);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/grid.xhtml";
	}
}
