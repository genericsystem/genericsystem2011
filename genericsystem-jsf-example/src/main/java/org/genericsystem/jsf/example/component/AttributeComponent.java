package org.genericsystem.jsf.example.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractAttributeComponent;

public class AttributeComponent extends AbstractAttributeComponent {

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}
}
