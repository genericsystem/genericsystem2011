package org.genericsystem.jsf.example.component;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

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

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		// TODO Auto-generated method stub
		return false;
	}

}
