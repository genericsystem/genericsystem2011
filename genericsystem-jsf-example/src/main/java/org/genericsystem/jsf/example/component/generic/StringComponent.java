package org.genericsystem.jsf.example.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

public class StringComponent extends AbstractGenericComponent {

	private String value;

	public StringComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		this.value = generic.toString();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String instance) {
		this.value = instance;
	}

	@Override
	public String getXhtmlPath() {
		return "string.xhtml";
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
