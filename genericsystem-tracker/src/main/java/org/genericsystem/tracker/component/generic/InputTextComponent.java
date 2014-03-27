package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.annotation.DateFormat;

public class InputTextComponent extends AbstractGenericComponent implements ValuedComponent {

	private String newValue;

	public InputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editInputText();
	}

	public void editInputText() {
		setNewValue(Objects.toString(((GenericComponent) this.getParent()).getGeneric().getValue((Attribute) getGeneric())));
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

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String str) {
		if (str != null)
			this.newValue = str;
	}

	@Override
	public String getXhtmlPath() {
		return "inputText.xhtml";
	}
}
