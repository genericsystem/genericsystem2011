package org.genericsystem.jsf.example.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractAttributeComponent;
import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;

public class TypeComponent extends AbstractTypeComponent {

	public TypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	@Override
	public boolean isSelected(Attribute attribute) {
		Serializable value = attribute.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/type.xhtml";
	}

	@Override
	public AbstractAttributeComponent buildComponent(Attribute attribute) {
		return new AttributeComponent(this, attribute);
	}
}
