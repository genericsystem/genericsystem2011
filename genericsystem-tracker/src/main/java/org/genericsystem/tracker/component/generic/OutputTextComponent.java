package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.tracker.component.AbstractComponent;

public class OutputTextComponent extends AbstractGenericComponent {

	private String value;

	public OutputTextComponent(AbstractComponent parent, Generic generic) {
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
		return "outputText.xhtml";
	}
}
