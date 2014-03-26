package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

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
	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<AbstractGenericComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public String getXhtmlPath() {
		return "outputText.xhtml";
	}
}
