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
import org.genericsystem.tracker.component.PanelGridComponent;

public class CommandButtonComponent extends AbstractGenericComponent {

	public CommandButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return Collections.emptyList();
	}

	public void select() {
		((PanelGridComponent) this.getParent()).select(getGeneric());
	}

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/commandbutton.xhtml";
	}

}
