package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.annotation.DateFormat;

public class InputTextComponent extends AbstractGenericComponent {

	private String value;

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editInputText();
	}

	public void editInputText() {
		setValue(Objects.toString(((GenericComponent) this.getParent()).getGeneric().getValue((Attribute) getGeneric())));
	}

	// TODO validator removed because of static generalized validator for all fields - bad implementation
	public String getValidatorId() {
		return getGeneric().getClass().getAnnotation(DateFormat.class) != null ? "dateValidator" : "";
	}

	public boolean isValidator() {
		return getGeneric().getClass().getAnnotation(DateFormat.class) != null;
	}

	// ENDTODO

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

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public String getXhtmlPath() {
		return "inputText.xhtml";
	}
}
