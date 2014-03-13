package org.genericsystem.jsf.example.component.generic;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

public class RowComponent extends AbstractGenericComponent {

	public RowComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new StringComponent(parent, getGeneric()), getGeneric().isRelation() ? new SelectItemComponent(parent, getGeneric()) : new InputTextComponent(parent, getGeneric()));
	}

	@Override
	public String getXhtmlPath() {
		return "row.xhtml";
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
