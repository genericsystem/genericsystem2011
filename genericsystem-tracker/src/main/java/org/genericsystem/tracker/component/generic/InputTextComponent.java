package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.tracker.annotation.DateFormat;
import org.genericsystem.tracker.component.AbstractComponent;

public class InputTextComponent extends AbstractGenericComponent {

	private String value;

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editInputText();
	}

	public void editInputText() {
		setValue(Objects.toString(this.<AbstractGenericComponent> getParent().getGeneric().getValue((Attribute) getGeneric())));
	}

	public String getValidatorId() {
		log.info("**-->" + getGeneric().getClass().getAnnotation(DateFormat.class));
		return getGeneric().getClass().getAnnotation(DateFormat.class) != null ? "dateValidator" : "";
	}

	public boolean isValidator() {
		log.info("-->" + getGeneric().getClass().getAnnotation(DateFormat.class));
		return getGeneric().getClass().getAnnotation(DateFormat.class) != null;
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

}
