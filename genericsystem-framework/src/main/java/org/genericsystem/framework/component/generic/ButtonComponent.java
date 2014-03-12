package org.genericsystem.framework.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class ButtonComponent extends AbstractGenericComponent {

	public ButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<AbstractAttributeComponent> initChildren() {
		return Collections.emptyList();
	}

	public void select() {
		((AbstractSelectorComponent) this.getParent()).select(getGeneric());
	}

	@Override
	public String getXhtmlPath() {
		return "button.xhtml";
	}

}
