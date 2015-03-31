package org.genericsystem.jsf.example.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class AttributeComponent extends AbstractValuedGenericComponent {

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public boolean isRelation() {
		return generic.isRelation();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<TypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public List<InstanceRow> getTargetRows() {
		return this.<TypeComponent> getParent().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).getAllInstances().<InstanceRow> project(instance -> new InstanceRow(instance));
	}

	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	public boolean isDirty() {
		return true;
	}

	public void setDirty(boolean isDirty) {
	}

}
