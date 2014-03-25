package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;

public class TextAreaComponent extends AbstractGenericComponent {

	private String value;

	public TextAreaComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getXhtmlPath() {
		return "textArea.xhtml";
	}

}
