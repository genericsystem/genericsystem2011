package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.tracker.component.AbstractComponent;
import org.genericsystem.tracker.component.SelectorComponent;

public class ButtonComponent extends AbstractGenericComponent {

	public ButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return Collections.emptyList();
	}

	public void select() {
		((SelectorComponent) this.getParent()).select(getGeneric());
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/button.xhtml";
	}

}
