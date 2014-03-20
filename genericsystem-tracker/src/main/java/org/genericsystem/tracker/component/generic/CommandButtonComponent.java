package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.tracker.component.AbstractComponent;
import org.genericsystem.tracker.component.PanelGridComponent;

public class CommandButtonComponent extends AbstractGenericComponent {

	public CommandButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return Collections.emptyList();
	}

	public void select() {
		((PanelGridComponent) this.getParent()).select(getGeneric());
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/commandbutton.xhtml";
	}

}
