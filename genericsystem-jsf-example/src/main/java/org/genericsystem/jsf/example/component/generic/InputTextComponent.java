package org.genericsystem.jsf.example.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;

public class InputTextComponent extends AbstractGenericComponent {

	private String value;

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editInputText();
	}

	public void editInputText() {
		setValue(Objects.toString(this.<EditComponent> getParent().getGeneric().getValue((Attribute) getGeneric())));
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getXhtmlPath() {
		return "inputText.xhtml";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
