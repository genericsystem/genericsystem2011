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

public class OutputTextComponent extends AbstractGenericComponent implements ValuedComponent {
	private final String newValue;

	public OutputTextComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		this.newValue = generic.toString();
	}

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

	@Override
	public String getXhtmlPath() {
		return "outputText.xhtml";
	}
}
